//
//  ZBHttpRequestController.h
//  Common-C-Library
//
//  Created by Alex on 2018/2/27.
//  Copyright © 2018年 qpidnetwork. All rights reserved.
//

#ifndef ZBHttpRequestController_h
#define ZBHttpRequestController_h
#include "ZBHttpLoginTask.h"
#include "ZBHttpUpdateTokenIdTask.h"
#include "ZBHttpGetVerificationCodeTask.h"
#include "ZBHttpLiveFansListTask.h"
#include "ZBHttpGetNewFansBaseInfoTask.h"
#include "ZBHttpGetAllGiftListTask.h"
#include "ZBHttpGiftListTask.h"
#include "ZBHttpGetGiftDetailTask.h"
#include "ZBHttpGetEmoticonListTask.h"
#include "ZBHttpDealTalentRequestTask.h"
#include "ZBHttpManHandleBookingListTask.h"
#include "ZBHttpAcceptScheduledInviteTask.h"
#include "ZBHttpRejectScheduledInviteTask.h"
#include "ZBHttpManBookingUnreadUnhandleNumTask.h"
#include "ZBHttpGetScheduledAcceptNumTask.h"
#include "ZBHttpAcceptInstanceInviteTask.h"
#include "ZBHttpRejectInstanceInviteTask.h"
#include "ZBHttpGetConfigTask.h"
#include "ZBHttpGetTodayCreditTask.h"
#include "ZBHttpServerSpeedTask.h"
#include "ZBHttpCrashFileTask.h"
#include "ZBHttpCancelInstantInviteUserTask.h"
#include "ZBHttpSetAutoPushTask.h"
#include "ZBHttpSetRoomCountDownTask.h"
#include <common/KSafeMap.h>

#include <stdio.h>

#include <string>
using namespace std;

#define HTTPREQUEST_INVALIDREQUESTID	0

class IZBHttpTask;
typedef KSafeMap<IZBHttpTask*, IZBHttpTask*> RequestMap;

class ZBHttpRequestController : public IZBHttpTaskCallback {
public:
    ZBHttpRequestController();
    ~ZBHttpRequestController();
    
    void Stop(long long requestId);

    /**
     *  2.1.登陆接口
     *
     * @param pHttpRequestManager           http管理器
     * @param anchorId                      主播ID
     * @param password                      密码
     * @param code                          验证码
     * @param deviceid                      设备唯一标识
     * @param model                         设备型号（格式：设备型号－系统版本号）
     * @param manufacturer                  制造厂商
     * @param callback                      接口回调
     *
     *  @return                             成功请求Id
     */
    long long     ZBLogin(
                        HttpRequestManager *pHttpRequestManager,
                        const string& anchorId,
                        const string& password,
                        const string& code,
                        const string& deviceid,
                        const string& model,
                        const string& manufacturer,
                        IRequestZBLoginCallback* callback = NULL
                        );
    
    /**
     *  2.2.上传tokenid接口
     *
     * @param pHttpRequestManager           http管理器
     * @param tokenId                       用于Push Notification的ID
     * @param callback                      接口回调
     *
     *  @return                             成功请求Id
     */
    long long     ZBUpdateTokenId(
                          HttpRequestManager *pHttpRequestManager,
                          const string& tokenId,
                          IRequestZBUpdateTokenIdCallback* callback = NULL
                          );
    
    /**
     *  2.3.获取验证码
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetVerificationCode(
                                  HttpRequestManager *pHttpRequestManager,
                                  IRequestZBGetVerificationCodeCallback* callback = NULL
                                  );
    
    /**
     *  3.1.获取直播间观众列表
     *
     * @param pHttpRequestManager           http管理器
     * @param roomId                        直播间ID
     * @param start                         起始，用于分页，表示从第几个元素开始获取
     * @param step                          步长，用于分页，表示本次请求获取多少个元素
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBLiveFansList(
                           HttpRequestManager *pHttpRequestManager,
                           const string& roomId,
                           int start,
                           int step,
                           IRequestZBLiveFansListCallback* callback = NULL
                           );
    
    /**
     *   3.2.获取指定观众信息
     *
     * @param pHttpRequestManager           http管理器
     * @param userId                        观众ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetNewFansBaseInfo(
                                 HttpRequestManager *pHttpRequestManager,
                                 const string& userId,
                                 IRequestZBGetNewFansBaseInfoCallback* callback = NULL
                                 );
    
    /**
     *  3.3.获取礼物列表
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetAllGiftList(
                             HttpRequestManager *pHttpRequestManager,
                             IRequestZBGetAllGiftListCallback* callback = NULL
                             );
    
    /**
     *  3.4.获取主播直播间礼物列表
     *
     * @param pHttpRequestManager           http管理器
     * @param roomId                        直播间ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGiftList(
                       HttpRequestManager *pHttpRequestManager,
                       const string& roomId,
                       IRequestZBGiftListCallback* callback = NULL
                       );
    
    /**
     *  3.5.获取指定礼物详情（用于观众端／主播端在直播间收到《获取礼物列表》没有礼物时，获取指定礼物详情来显示）
     *
     * @param pHttpRequestManager           http管理器
     * @param giftId                        礼物ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetGiftDetail(
                            HttpRequestManager *pHttpRequestManager,
                            const string& giftId,
                            IRequestZBGetGiftDetailCallback* callback = NULL
                            );
    
    /**
     *  3.6.获取文本表情列表（用于观众端/主播端获取文本聊天礼物列表）
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetEmoticonList(
                              HttpRequestManager *pHttpRequestManager,
                              IRequestZBGetEmoticonListCallback* callback = NULL
                              );
    
    /**
     *  3.7.主播回复才艺点播邀请（用于主播接受/拒绝观众发出的才艺点播邀请）
     *
     * @param pHttpRequestManager           http管理器
     * @param talentInviteId                才艺点播邀请ID
     * @param status                        处理结果（1：同意，2：拒绝）
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBDealTalentRequest(
                                HttpRequestManager *pHttpRequestManager,
                                const string talentInviteId,
                                ZBTalentReplyType status,
                                IRequestZBDealTalentRequestCallback* callback = NULL
                                );
    
    /**
     *  3.8.设置主播公开直播间自动邀请状态
     *
     * @param pHttpRequestManager           http管理器
     * @param status                        处理结果（SETPUSHTYPE_CLOSE：关闭，SETPUSHTYPE_START：启动）
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBSetAutoPush(
                                  HttpRequestManager *pHttpRequestManager,
                                  ZBSetPushType status,
                                  IRequestZBSetAutoPushCallback* callback = NULL
                                  );
    
    /**
     *  4.1.获取预约邀请列表
     *
     * @param pHttpRequestManager           http管理器
     * @param type                          列表类型（1:等待主播处理 2:等待观众处理  3:已确认 4：历史
     * @param start                         起始，用于分页，表示从第几个元素开始获取
     * @param step                          步长，用于分页，表示本次请求获取多少个元素
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBManHandleBookingList(
                                   HttpRequestManager *pHttpRequestManager,
                                   ZBBookingListType type,
                                   int start,
                                   int step,
                                   IRequestZBManHandleBookingListCallback* callback = NULL
                                   );
    
    /**
     *  4.2.主播接受预约(邀请主播接受观众发起的预约邀请)
     *
     * @param pHttpRequestManager           http管理器
     * @param inviteId                      邀请ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBAcceptScheduledInvite(
                            HttpRequestManager *pHttpRequestManager,
                            const string& inviteId,
                            IRequestZBAcceptScheduledInviteCallback* callback = NULL
                            );
    
    /**
     *  4.3.主播拒绝预约邀请(主播拒绝观众发起的预约邀请)
     *
     * @param pHttpRequestManager           http管理器
     * @param invitationId                  邀请ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBRejectScheduledInvite(
                                          HttpRequestManager *pHttpRequestManager,
                                          const string& invitationId,
                                          IRequestZBRejectScheduledInviteCallback* callback = NULL
                                          );
    
    /**
     *  4.4.获取预约邀请未读或待处理数量
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetScheduleListNoReadNum(
                                      HttpRequestManager *pHttpRequestManager,
                                      IRequestZBGetScheduleListNoReadNumCallback* callback = NULL
                                      );
    
    /**
     *  4.5.获取已确认的预约数(用于主播端获取已确认的预约数量)
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetScheduledAcceptNum(
                                     HttpRequestManager *pHttpRequestManager,
                                     IRequestZBGetScheduledAcceptNumCallback* callback = NULL
                                     );
    
    /**
     *  4.6.主播接受立即私密邀请(用于主播接受观众发送的立即私密邀请)
     *
     * @param pHttpRequestManager           http管理器
     * @param inviteId                      邀请ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBAcceptInstanceInvite(
                                   HttpRequestManager *pHttpRequestManager,
                                   const string& inviteId,
                                   IRequestZBAcceptInstanceInviteCallback* callback = NULL
                                   );
    
    /**
     *  4.7.主播拒绝立即私密邀请(用于主播拒绝观众发送的立即私密邀请)
     *
     * @param pHttpRequestManager           http管理器
     * @param inviteId                      邀请ID
     * @param rejectReason                  拒绝理由（可无）
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBRejectInstanceInvite(
                                   HttpRequestManager *pHttpRequestManager,
                                   const string& inviteId,
                                   const string& rejectReason,
                                   IRequestZBRejectInstanceInviteCallback* callback = NULL
                                   );
    
    /**
     *  4.8.主播取消已发的立即私密邀请
     *
     * @param pHttpRequestManager           http管理器
     * @param inviteId                      邀请ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBCancelInstantInviteUser(
                                     HttpRequestManager *pHttpRequestManager,
                                     const string& inviteId,
                                     IRequestZBCancelInstantInviteUserCallback* callback = NULL
                                     );
    
    /**
     *  4.9.设置直播间为开始倒数
     *
     * @param pHttpRequestManager           http管理器
     * @param inviteId                      直播间ID
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBSetRoomCountDown(
                                        HttpRequestManager *pHttpRequestManager,
                                        const string& roomId,
                                        IRequestZBSetRoomCountDownCallback* callback = NULL
                                        );
    
    /**
     *  5.1.同步配置
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetConfig(
                        HttpRequestManager *pHttpRequestManager,
                        IRequestZBGetConfigCallback* callback = NULL
                        );
    
    /**
     *  5.2.获取收入信息
     *
     * @param pHttpRequestManager           http管理器
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBGetTodayCredit(
                          HttpRequestManager *pHttpRequestManager,
                          IRequestZBGetTodayCreditCallback* callback = NULL
                          );
    
    /**
     * 5.3.提交流媒体服务器测速结果
     *
     * @param pHttpRequestManager           http管理器
     * @param sid                           流媒体服务器ID
     * @param res                           http请求完成时间（毫秒）
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBServerSpeed(
                          HttpRequestManager *pHttpRequestManager,
                          const string& sid,
                          int res,
                          IRequestZBServerSpeedCallback* callback = NULL
                          );
    
    /**
     * 5.4.提交crash dump文件
     *
     * @param pHttpRequestManager           http管理器）
     * @param deviceId                      设备唯一标识
     * @param crashFile                     crash dump文件zip包二进制流（zip密钥：Qpid_Dating）
     * @param callback                      接口回调
     *
     * @return                              成功请求Id
     */
    long long ZBCrashFile(
                        HttpRequestManager *pHttpRequestManager,
                        const string& deviceId,
                        const string& crashFile,
                        IRequestZBCrashFileCallback* callback = NULL
                        );
    
    
private:
    void OnTaskFinish(IZBHttpTask* task);
    
private:
    RequestMap mRequestMap;
};

#endif /* ZBHttpRequestController_h */