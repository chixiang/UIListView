//
//  TPGestureTableViewCell.h
//  UIListView
//
//  Created by Anonymity on ？？-6-28.
//  Copyright (c) 20？？年 ？？. All rights reserved.
//

#import "AsyncImageViewUI.h"
#import <CommonCrypto/CommonDigest.h>
@interface AsyncImageViewUI ()
- (void) downloadImage:(NSString *)imageURL;
@end

@implementation AsyncImageViewUI
@synthesize requestUI = _requestUI;
@synthesize needClipUI = _needClipUI;

- (void) dealloc {
	self.requestUI.delegate = nil;
    [self cancelDownload];
}

- (void) loadImage:(NSString *)imageURL {
	if (![imageURL isKindOfClass:[NSString class]] || [imageURL length]==0){
		return;
	}
    [self loadImage:imageURL withPlaceholdImage:self.image];
}

- (void) loadImage:(NSString *)imageURL withPlaceholdImage:(UIImage *)placeholdImage {
    self.image = placeholdImage;
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
        UIImage *image = [self getImageInCacheWithURLStr:imageURL];
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (image) {
                self.image = image;
			    if (self.needClipUI) {
					CGPoint center = self.center;
					self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, image.size.width, image.size.height);
					self.center = center;
				}
			} else {
                [self downloadImage:imageURL];
			}
        });
    });
}

- (void) cancelDownload {
    [self.requestUI cancel];
    self.requestUI = nil;
}

#pragma mark - 
#pragma mark private downloads
#pragma mark -

- (void) downloadImage:(NSString *)imageURL {
    [self cancelDownload];
    __weak AsyncImageViewUI *asyImg = self;
	NSString * newImageURL = [imageURL stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    self.requestUI = [UZASIHTTPRequest requestWithURL:[NSURL URLWithString:newImageURL]];
	NSString *fileName = [NSString stringWithFormat:@"%@.png",[self md5:imageURL]];
    [self.requestUI setDownloadDestinationPath:[self getImagePathInCache:fileName]];
    [self.requestUI setDelegate:self];
    [self.requestUI setCompletionBlock:^(void){
		asyImg.requestUI.delegate = nil;
        asyImg.requestUI = nil;
		dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
		dispatch_async(queue, ^{
			UIImage *image = [asyImg getImageInCacheWithURLStr:imageURL];
			dispatch_sync(dispatch_get_main_queue(), ^{
				if (image) {
					if (asyImg.needClipUI) {
						CGPoint center = asyImg.center;
						asyImg.frame = CGRectMake(asyImg.frame.origin.x, asyImg.frame.origin.y, image.size.width, image.size.height);
						asyImg.center = center;
					}
					asyImg.alpha = 0;
					[UIView beginAnimations:nil context:NULL];
					[UIView setAnimationDuration:0.5];
					asyImg.image = image;
					asyImg.alpha = 1.0;
					[UIView commitAnimations];
				}
            });
		});}];
    [self.requestUI setFailedBlock:^(void){
        [asyImg.requestUI cancel];
        asyImg.requestUI.delegate = nil;
        asyImg.requestUI = nil;
	}];
    [self.requestUI startAsynchronous];
}

- (UIImage*)getImageInCacheWithURLStr:(NSString *)inURLStr{
	UIImage *image = nil;
	NSString *imageName = nil;
	if ((![inURLStr isKindOfClass:[NSString class]]) || ([inURLStr length] == 0)){
		return nil;
	}
	imageName = [NSString stringWithFormat:@"%@.png",[self md5:inURLStr]];
	if ([self imageIsExistInCache:imageName]) {
		image = [UIImage imageWithContentsOfFile:[self getImagePathInCache:imageName]];
	}
	return image;
}

- (NSString *)md5:(NSString *)str{
	const char *cStr = [str UTF8String];
	unsigned char result[16];
	CC_MD5( cStr, (unsigned)strlen(cStr), result );
	return [NSString stringWithFormat:
			@"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
			result[0], result[1], result[2], result[3],
			result[4], result[5], result[6], result[7],
			result[8], result[9], result[10], result[11],
			result[12], result[13], result[14], result[15]
			];
	
}

- (BOOL)imageIsExistInCache:(NSString*)inImageName {
	NSFileManager *manager = [NSFileManager defaultManager];
	NSString *dir = [NSHomeDirectory() stringByAppendingPathComponent:@"Documents/imageCache"];
	if (![manager fileExistsAtPath:dir]) {
		[manager createDirectoryAtPath:dir withIntermediateDirectories:YES attributes:nil error:nil];
	}
	NSString *path = [self getImagePathInCache:inImageName];
	return [manager fileExistsAtPath:path];
}

- (NSString *)getImagePathInCache:(NSString *)inImageName{
	return [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"Documents/imageCache/%@",inImageName]];
}
@end
