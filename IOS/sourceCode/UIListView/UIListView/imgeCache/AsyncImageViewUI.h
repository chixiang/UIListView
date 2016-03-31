//
//  TPGestureTableViewCell.h
//  UIListView
//
//  Created by Anonymity on ？？-6-28.
//  Copyright (c) 20？？年 ？？. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "UZASIHTTPRequest.h"
#import <CommonCrypto/CommonDigest.h>
@interface AsyncImageViewUI : UIImageView
<ASIHTTPRequestDelegate>

@property (nonatomic,retain) UZASIHTTPRequest *requestUI;
@property (nonatomic) BOOL needClipUI;

- (void) loadImage:(NSString *)imageURL;
- (void) loadImage:(NSString *)imageURL withPlaceholdImage:(UIImage *)image;
- (void) cancelDownload;

@end
