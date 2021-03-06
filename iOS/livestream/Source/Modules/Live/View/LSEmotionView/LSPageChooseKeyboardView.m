//
//  LSPageChooseKeyboardView.m
//  dating
//
//  Created by Max on 16/10/14.
//  Copyright © 2016年 qpidnetwork. All rights reserved.
//

#import "LSPageChooseKeyboardView.h"
#import "LiveBundle.h"

@interface LSPageChooseKeyboardView ()
@property (nonatomic,strong) UIView* selectedButtonBackgroundView;
@property (nonatomic,assign) BOOL isShowed;
@end

@implementation LSPageChooseKeyboardView
+ (instancetype)LSPageChooseKeyboardView:(id)owner {
    NSArray *nibs = [[LiveBundle mainBundle] loadNibNamedWithFamily:@"LSPageChooseKeyboardView" owner:owner options:nil];
    LSPageChooseKeyboardView* view = [nibs objectAtIndex:0];
    
    view.pagingScrollView.pagingViewDelegate = view;
    
    return view;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)reloadData {
    
    if (self.isShowed) {
        return;
    }
    self.isShowed = YES;
    [self.buttonsView removeAllSubviews];
    
    if( [self.buttons count] > 0 ) {
        self.selectedButtonBackgroundView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.selectedButtonBackgroundView setBackgroundColor:Color(246, 246, 246, 1.0)];
//        [self.selectedButtonBackgroundView setBackgroundColor:[UIColor darkGrayColor]];
        [self.buttonsView addSubview:self.selectedButtonBackgroundView];
        
        NSInteger index = 0;
        for(UIButton* button in self.buttons) {
            button.tag = index++;
            [button addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
            [button setSelected:NO];

            [button removeFromSuperview];
            [self.buttonsView addSubview:button];
        }
        
        [[self.buttons objectAtIndex:0] setSelected:YES];

        [self setNeedsLayout];
        [self layoutIfNeeded];

        [self.pagingScrollView displayPagingViewAtIndex:0 animated:NO];
        self.pagingScrollView.scrollEnabled = NO;
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat itemWidth = self.buttonsView.frame.size.width;
    CGFloat itemHeight = self.buttonsView.frame.size.height;
    
    NSInteger curIndex = 0;
    for(UIButton* button in self.buttons) {
        button.frame = CGRectMake(curIndex * (itemWidth * 0.5), 0, itemWidth * 0.5, itemHeight);
        curIndex++;
        
        if( button.selected ) {
            self.selectedButtonBackgroundView.frame = button.frame;
        }
    }
}

- (IBAction)buttonAction:(id)sender {
    NSInteger index = ((UIButton *)sender).tag;

    for(UIButton* button in self.buttons) {
        if( sender == button ) {
            [button setSelected:YES];
            self.selectedButtonBackgroundView.frame = button.frame;
        } else {
            [button setSelected:NO];
        }
    }
    
    [self.pagingScrollView displayPagingViewAtIndex:index animated:NO];
}

// 切换选中按钮及相应界面
- (void)toggleButtonSelect:(NSInteger)index {
    for (UIButton *btn in self.buttons) {
        if (index == btn.tag) {
            [btn setSelected:YES];
            self.selectedButtonBackgroundView.frame = btn.frame;
        } else {
            [btn setSelected:NO];
        }
    }
    [self.pagingScrollView displayPagingViewAtIndex:index animated:NO];
}

#pragma mark - 画廊回调 (LSPZPagingScrollViewDelegate)
- (Class)pagingScrollView:(LSPZPagingScrollView *)pagingScrollView classForIndex:(NSUInteger)index {

    Class cls = [UIView class];
    if( [self.delegate respondsToSelector:@selector(LSPageChooseKeyboardView:classForIndex:)] ) {
        cls = [self.delegate LSPageChooseKeyboardView:self classForIndex:index];
    }
    return cls;
}

- (NSUInteger)pagingScrollViewPagingViewCount:(LSPZPagingScrollView *)pagingScrollView {
    NSUInteger count = 0;
    if( [self.delegate respondsToSelector:@selector(pagingViewCount:)] ) {
        count = [self.delegate pagingViewCount:self];
    }
    return count;
}

- (UIView *)pagingScrollView:(LSPZPagingScrollView *)pagingScrollView pageViewForIndex:(NSUInteger)index {
    UIView *view = nil;
    if( [self.delegate respondsToSelector:@selector(LSPageChooseKeyboardView:pageViewForIndex:)] ) {
        view = [self.delegate LSPageChooseKeyboardView:self pageViewForIndex:index];
    }
    return view;
}

- (void)pagingScrollView:(LSPZPagingScrollView *)pagingScrollView preparePageViewForDisplay:(UIView *)pageView forIndex:(NSUInteger)index {
    if( [self.delegate respondsToSelector:@selector(LSPageChooseKeyboardView:preparePageViewForDisplay:forIndex:)] ) {
        [self.delegate LSPageChooseKeyboardView:self preparePageViewForDisplay:pageView forIndex:index];
    }
}

- (void)pagingScrollView:(LSPZPagingScrollView *)pagingScrollView didShowPageViewForDisplay:(NSUInteger)index {
    for(UIButton* button in self.buttons) {
        if( button.tag == index ) {
            [button setSelected:YES];
            self.selectedButtonBackgroundView.frame = button.frame;
        } else {
            [button setSelected:NO];
        }
    }
    
    if( [self.delegate respondsToSelector:@selector(LSPageChooseKeyboardView:didShowPageViewForDisplay:)] ) {
        [self.delegate LSPageChooseKeyboardView:self didShowPageViewForDisplay:index];
    }
}

@end
