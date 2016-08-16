/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import com.jqb.shop.Setting;
import com.jqb.shop.entity.ProductCategoryImage;
import com.jqb.shop.entity.ProductImage;
import com.jqb.shop.plugin.StoragePlugin;
import com.jqb.shop.service.ProductCategoryImageService;
import com.jqb.shop.service.ProductImageService;
import com.jqb.shop.util.FreemarkerUtils;
import com.jqb.shop.util.ImageUtils;
import com.jqb.shop.util.SettingUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.*;

/**
 * Service - 商品图片
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("productCategoryImageServiceImpl")
public class ProductCategoryImageServiceImpl implements ProductCategoryImageService, ServletContextAware {

	/** 目标扩展名 */
	private static final String DEST_EXTENSION = "jpg";
	/** 目标文件类型 */
	private static final String DEST_CONTENT_TYPE = "image/jpeg";

	/** servletContext */
	private ServletContext servletContext;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	@Resource
	private List<StoragePlugin> storagePlugins;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * 添加图片处理任务
	 * 
	 * @param sourcePath
	 *            原图片上传路径
	 * @param largePath
	 *            图片文件(大)上传路径
	 * @param mediumPath
	 *            图片文件(小)上传路径
	 * @param thumbnailPath
	 *            图片文件(缩略)上传路径
	 * @param tempFile
	 *            原临时文件
	 * @param contentType
	 *            原文件类型
	 */
	private void addTask(final String sourcePath, final String largePath, final String mediumPath, final String thumbnailPath, final File tempFile, final String contentType) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					Collections.sort(storagePlugins);
					for (StoragePlugin storagePlugin : storagePlugins) {
						if (storagePlugin.getIsEnabled()) {
							Setting setting = SettingUtils.get();
							String tempPath = System.getProperty("java.io.tmpdir");
							//File watermarkFile = new File(servletContext.getRealPath(setting.getWatermarkImage()));
							File largeTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							File mediumTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							File thumbnailTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							try {
								ImageUtils.zoom(tempFile, largeTempFile, setting.getLargeProductImageWidth(), setting.getLargeProductImageHeight());
								//ImageUtils.addWatermark(largeTempFile, largeTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
								ImageUtils.zoom(tempFile, mediumTempFile, setting.getMediumProductImageWidth(), setting.getMediumProductImageHeight());
								//ImageUtils.addWatermark(mediumTempFile, mediumTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
								ImageUtils.zoom(tempFile, thumbnailTempFile, setting.getThumbnailProductImageWidth(), setting.getThumbnailProductImageHeight());
								storagePlugin.upload(sourcePath, tempFile, contentType);
								storagePlugin.upload(largePath, largeTempFile, DEST_CONTENT_TYPE);
								storagePlugin.upload(mediumPath, mediumTempFile, DEST_CONTENT_TYPE);
								storagePlugin.upload(thumbnailPath, thumbnailTempFile, DEST_CONTENT_TYPE);
							} finally {
								FileUtils.deleteQuietly(tempFile);
								FileUtils.deleteQuietly(largeTempFile);
								FileUtils.deleteQuietly(mediumTempFile);
								FileUtils.deleteQuietly(thumbnailTempFile);
							}
							break;
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void build(ProductCategoryImage productCategoryImage) {
		MultipartFile multipartFile = productCategoryImage.getFile();
		if (multipartFile != null && !multipartFile.isEmpty()) {
			try {
				Setting setting = SettingUtils.get();
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("uuid", UUID.randomUUID().toString());
				String uploadPath = FreemarkerUtils.process(setting.getImageUploadPath(), model);
				String uuid = UUID.randomUUID().toString();
				String sourcePath = uploadPath + uuid + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
				String largePath = uploadPath + uuid + "-large." + DEST_EXTENSION;
				String mediumPath = uploadPath + uuid + "-medium." + DEST_EXTENSION;
				String thumbnailPath = uploadPath + uuid + "-thumbnail." + DEST_EXTENSION;

				Collections.sort(storagePlugins);
				for (StoragePlugin storagePlugin : storagePlugins) {
					if (storagePlugin.getIsEnabled()) {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "/upload_" + UUID.randomUUID() + ".tmp");
						if (!tempFile.getParentFile().exists()) {
							tempFile.getParentFile().mkdirs();
						}
						multipartFile.transferTo(tempFile);
						addTask(sourcePath, largePath, mediumPath, thumbnailPath, tempFile, multipartFile.getContentType());
						productCategoryImage.setSource(storagePlugin.getUrl(sourcePath));
						productCategoryImage.setLarge(storagePlugin.getUrl(largePath));
						productCategoryImage.setMedium(storagePlugin.getUrl(mediumPath));
						productCategoryImage.setThumbnail(storagePlugin.getUrl(thumbnailPath));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
