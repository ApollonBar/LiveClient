package net.qdating.publisher;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.view.Surface;
import net.qdating.LSConfig;
import net.qdating.LSConfig.FillMode;
import net.qdating.filter.LSImageFilter;
import net.qdating.utils.Log;

/**
 * 视频采集器
 * @author max
 *
 */
@SuppressWarnings("deprecation")
public class LSVideoCapture implements ILSVideoPreviewCallback {
	private final int INVALID_FRAMERATE = -1;
	
	private GLSurfaceView previewSurfaceView;
	private Camera mCamera;
	private ILSVideoCaptureCallback captureCallback;
	private int rotation;
	private int previewRotation;
	private LSPublishConfig publishConfig;

	/**
	 * 是否使用前置摄像头
	 */
	private boolean frontCamera = true;
	/**
	 * 标记camera是否预览中
	 */
	private boolean previewRunning = false;
	/**
	 * 预览渲染器
	 */
	private LSVideoCaptureRenderer previewRenderer = null;
	
	public LSVideoCapture() {
	}
	
	@SuppressLint("NewApi") 
	public boolean init(GLSurfaceView surfaceView, ILSVideoCaptureCallback callback, int rotation, FillMode fillMode, boolean useHardEncoder, LSPublishConfig publishConfig) {
		boolean bFlag = false;
		
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::init( this : 0x%x )", hashCode()));

		this.publishConfig = publishConfig;

		// 创建预览渲染器
		this.previewRenderer = new LSVideoCaptureRenderer(this, fillMode, useHardEncoder, publishConfig);
		this.previewRenderer.init();
		
		// 设置GL预览界面, 按照顺序调用, 否则crash
		this.previewSurfaceView = surfaceView;
		this.previewSurfaceView.setEGLContextClientVersion(2);
		this.previewSurfaceView.setRenderer(previewRenderer);
		this.previewSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		this.previewSurfaceView.setPreserveEGLContextOnPause(true);
		
		this.captureCallback = callback;
		this.rotation = rotation;
		
		bFlag = true;
		
		return bFlag;
	}
	
	public void uninit() {
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::uninit( this : 0x%x )", hashCode()));
		
		if( previewRunning ) {
			// 停止预览
			stopCapture();
			previewRunning = false;
		}

		if( previewRenderer != null ) {
			previewRenderer.uninit();
		}
	}

	/**
	 * 设置自定义滤镜
	 * @param customFilter 自定义滤镜
	 */
	public void setCustomFilter(LSImageFilter customFilter) {
		if( previewRenderer != null ) {
			previewRenderer.setCustomFilter(customFilter);
		}
	}

	/**
	 * 获取自定义滤镜
	 * @return 自定义滤镜
	 */
	public LSImageFilter getCustomFilter() {
		LSImageFilter filter = previewRenderer.getCustomFilter();
		return filter;
	}

	/**
	 * 设置自定义上传图片
	 * @param bitmap 图片
	 */
	public void setCaptureBitmap(Bitmap bitmap) {
		if( previewRenderer != null ) {
			previewRenderer.setCaptureBitmap(bitmap);
		}
	}

	public boolean start() {
		boolean bFlag = true;
		
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::start( this : 0x%x )", hashCode()));
		
		if( !previewRunning ) {
			bFlag = startCapture();
			previewRunning = bFlag;
		}
		
		if( !bFlag ) {
			Log.e(LSConfig.TAG, String.format("LSVideoCapture::start( this : 0x%x, [Fail] )", hashCode()));
		}
		
		return bFlag;
	}
	
	@SuppressLint("NewApi") 
	public void stop() {
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::stop( this : 0x%x )", hashCode()));
		
		if( previewRunning ) {
			// 停止预览
			stopCapture();
			previewRunning = false;
		}
	}
	
	/**
	 * 切换前后摄像头
	 */
	public boolean rotateCamera() {
		boolean bFlag = true;
		
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::rotateCamera( this : 0x%x )", hashCode()));
		
		// 切换前后摄像头
		frontCamera = !frontCamera;
		
		if( previewRunning ) {
			// 停止预览
			stopCapture();
			// 重新开始预览
			bFlag = startCapture();
		}
		
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::rotateCamera( this : 0x%x, [%s] )", hashCode(), bFlag?"Success":"Fail"));
		
		return bFlag;
	}
	
	@SuppressLint("NewApi") 
	private boolean openCamera() {
		boolean bFlag = false;
		
		Log.i(LSConfig.TAG, String.format("LSVideoCapture::openCamera( this : 0x%x )", hashCode()));

		if( mCamera == null ) {
			int cameraIndex = getCamera(frontCamera);
			if( cameraIndex != -1 ) {
				try {
					// 打开摄像头
					mCamera = Camera.open(cameraIndex);
					if( mCamera != null ) {
						if( previewRenderer.getSurfaceTexture() != null ) {
							mCamera.setPreviewTexture(previewRenderer.getSurfaceTexture());
						}

						bFlag = true;

						// 获取采集参数
						Camera.Parameters parameters = mCamera.getParameters();

						// 设置采集的格式
//							int[] formats = getCameraPreviewFormats(mCamera);
//							parameters.setPreviewFormat(formats[0]);
						parameters.setPreviewFormat(ImageFormat.NV21);

						// 设置采集分辨率
						Size bestSize = getBestSuppportPreviewSize(mCamera);
						if( bestSize != null ) {
							parameters.setPreviewSize(bestSize.width, bestSize.height);
						} else {
							bFlag = false;
						}

						// 设置采集帧率
						int fps = getBestCameraPreviewFrameRate(mCamera);
						if( fps != INVALID_FRAMERATE ) {
							parameters.setPreviewFrameRate(fps);
//								parameters.setPreviewFpsRange(fps, fps);
						} else {
							bFlag = false;
						}

						// 设置采集参数
						mCamera.setParameters(parameters);

						int degree = getDegree();

						int result;
						Camera.CameraInfo info = new Camera.CameraInfo();
						Camera.getCameraInfo(cameraIndex, info);
						if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
							result = (info.orientation + degree) % 360;
							result = (360 - result) % 360;  // compensate the mirror
						} else {  // back-facing
							result = (info.orientation - degree + 360) % 360;
						}

						previewRotation = result;
						mCamera.setDisplayOrientation(previewRotation);

						mCamera.setErrorCallback(new Camera.ErrorCallback() {
							@Override
							public void onError(int error, Camera camera) {
							Log.e(LSConfig.TAG, String.format("LSVideoCapture::openCamera( this : 0x%x, [ErrorCallback], error : %d )", hashCode(), error));
							// 停止录制
							stop();
							captureCallback.onVideoCaptureError(error);
							}
						});
						if( captureCallback != null ) {
							captureCallback.onChangeRotation(previewRotation);
						}
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(LSConfig.TAG, String.format("LSVideoCapture::openCamera( this : 0x%x, [Exception], e : %s )", hashCode(), e.toString()));
				}
			}
		}

		
		if( !bFlag ) {
			Log.e(LSConfig.TAG, String.format("LSVideoCapture::openCamera( this : 0x%x, [Fail] )", hashCode()));
		}
		
		return bFlag;
	}
	
	/**
	 * 开启实时预览
	 * @param
	 */
	private boolean startCapture() {
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::startCapture( this : 0x%x )", hashCode()));
		
		boolean bFlag = false;

		synchronized (this) {
			if (mCamera == null) {
				bFlag = openCamera();
			}

			if (bFlag && mCamera != null) {
				mCamera.startPreview();
				bFlag = true;
			}
		}
		
		return bFlag;
	}
	
	private boolean stopCapture() {
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::stopCapture( this : 0x%x )", hashCode()));
		
		boolean bFlag = false;

		synchronized (this) {
			if (mCamera != null) {
				// 停止预览
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				bFlag = true;
			}
		}

		return bFlag;
	}
	
	/**
	 * 查找摄像头
	 * @return
	 */
	private int getCamera(boolean isFront) {
		CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		int cameraIndex = -1;
		
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == (isFront?CameraInfo.CAMERA_FACING_FRONT:CameraInfo.CAMERA_FACING_BACK)) {
				cameraIndex = camIdx;
				break;
			}
		}

		Log.i(LSConfig.TAG, String.format("LSVideoCapture::getCamera( this : 0x%x, cameraIndex : %d )", hashCode(), cameraIndex));
		
		return cameraIndex;
	}
	
	/**
	 * 获取最合适的Preview size(大于设定大小，且最小buffer)
	 * @param camera
	 * @return
	 */
	private Size getBestSuppportPreviewSize(Camera camera) {
		Size bestSize = null;
		Camera.Parameters parameters = mCamera.getParameters();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
		if( previewSizes != null ) {
			for (Size size : previewSizes) {
				if( LSConfig.DEBUG ) {
					Log.i(LSConfig.TAG,
							String.format("LSVideoCapture::getBestSuppportPreviewSize( " +
									"this : 0x%x, " +
									"width : %d, " +
									"height : %d " +
									")",
									hashCode(),
									size.width,
									size.height
							)
					);
				}

				// 是否宽高互换
				if( isSwitchFrame() ) {
					if (size.width == publishConfig.videoCaptureHeight && size.height == publishConfig.videoCaptureWidth) {
						bestSize = size;
						break;
					}
				} else {
					if (size.width == publishConfig.videoCaptureWidth && size.height == publishConfig.videoCaptureHeight) {
						bestSize = size;
						break;
					}
				}
			}
		}
		
		if( bestSize != null ) {
			Log.i(LSConfig.TAG, String.format("LSVideoCapture::getBestSuppportPreviewSize( this : 0x%x, bestWidth : %d, bestHeight : %d )", hashCode(), bestSize.width, bestSize.height));
			// 是否宽高互换
			if( isSwitchFrame() ) {
				previewRenderer.setOriginalSize(bestSize.height, bestSize.width);
			} else {
				previewRenderer.setOriginalSize(bestSize.width, bestSize.height);
			}
			
		} else {
			Log.e(LSConfig.TAG, String.format("LSVideoCapture::getBestSuppportPreviewSize( this : 0x%x, [Fail] )", hashCode()));
		}
		
		return bestSize;
	}
	
	/**
	 * 获取最合适的帧采集率
	 * @return
	 */
	private int getBestCameraPreviewFrameRate(Camera camera) {
		int bestFrameRate = INVALID_FRAMERATE;
		List<Integer> previewFrameRate = camera.getParameters().getSupportedPreviewFrameRates();
		for(Integer frameRate : previewFrameRate) {
			if( frameRate > bestFrameRate ) {
				bestFrameRate = frameRate;
			}
//			if( bestFrameRate > 10 ) {
//				bestFrameRate = 10;
//				break;
//			}
		}
		
		Log.w(LSConfig.TAG, String.format("LSVideoCapture::getBestCameraPreviewFrameRate( this : 0x%x, bestFrameRate : %d )", hashCode(), bestFrameRate));
		
		return bestFrameRate;
	}
	
	/**
	 * 获取旋转度数, 以竖屏为0
	 * @return
	 */
	private int getDegree() {
		int degree = 0;
		switch (rotation) {
			case Surface.ROTATION_0: {
				degree = 0; 
			}break;
			case Surface.ROTATION_90: {
				degree = 90; 
			}break;
			case Surface.ROTATION_180: {
				degree = 180; 
			}break;
			case Surface.ROTATION_270: {
				degree = 270; 
			}break;
		}
		Log.i(LSConfig.TAG, String.format("LSVideoCapture::getDegree( this : 0x%x, degree : %d )", hashCode(), degree));
		return degree;
	}
	
	private boolean isSwitchFrame() {
		return (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180);
	}
	
	/**
	 * 获取当前手机支持的预览格式列表
	 * @param camera
	 * @return
	 */
	@SuppressWarnings("unused")
	private int[] getCameraPreviewFormats(Camera camera){
		List<Integer> previewForamts = camera.getParameters().getSupportedPreviewFormats();
		int[] previewFormatArray = new int[previewForamts.size()];
		for(int i = 0; i < previewForamts.size(); i++){
			previewFormatArray[i] = previewForamts.get(i);
		}
		return previewFormatArray;
	}
	
	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		// TODO Auto-generated method stub
//		Log.d(LSConfig.TAG, String.format("LSVideoCapture::onFrameAvailable()"));
		// 已经获取到数据, 触发重绘
		previewSurfaceView.requestRender();
	}

	@Override
	public void onCreateTexture(SurfaceTexture surfaceTexture) {
		// TODO Auto-generated method stub
		Log.d(LSConfig.TAG, String.format("LSVideoCapture::onCreateTexture( this : 0x%x )", hashCode()));
		synchronized (this) {
			if( mCamera != null ) {
				try {
					mCamera.setPreviewTexture(surfaceTexture);
					if( previewRunning ) {
						Log.d(LSConfig.TAG, String.format("LSVideoCapture::onCreateTexture( this : 0x%x, [Camera start preview] )", hashCode()));
						mCamera.startPreview();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onRenderFrame(byte[] data, int size, int width, int height) {
		if( captureCallback != null ) {
			captureCallback.onVideoCapture(data, size, width, height);
		}
	}
}
