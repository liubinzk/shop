/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.plugin.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jqb.shop.FileInfo;
import com.jqb.shop.entity.PluginConfig;
import com.jqb.shop.plugin.StoragePlugin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * Plugin - 阿里云存储
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("ossPlugin")
public class OssPlugin extends StoragePlugin {

	@Override
	public String getName() {
		return "aliyun cun chu";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "SHOP++";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.shopxx.net";
	}

	@Override
	public String getInstallUrl() {
		return "oss/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "oss/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "oss/setting.jhtml";
	}

	@Override
	public void upload(String path, File file, String contentType) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String accessId = pluginConfig.getAttribute("accessId");
			String accessKey = pluginConfig.getAttribute("accessKey");
			String bucketName = pluginConfig.getAttribute("bucketName");
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				OSSClient ossClient = new OSSClient(accessId, accessKey);
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentType(contentType);
				objectMetadata.setContentLength(file.length());
				ossClient.putObject(bucketName, StringUtils.removeStart(path, "/"), inputStream, objectMetadata);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}

	@Override
	public String getUrl(String path) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String urlPrefix = pluginConfig.getAttribute("urlPrefix");
			return urlPrefix + path;
		}
		return null;
	}

	@Override
	public List<FileInfo> browser(String path) {
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String accessId = pluginConfig.getAttribute("accessId");
			String accessKey = pluginConfig.getAttribute("accessKey");
			String bucketName = pluginConfig.getAttribute("bucketName");
			String urlPrefix = pluginConfig.getAttribute("urlPrefix");
			try {
				OSSClient ossClient = new OSSClient(accessId, accessKey);
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
				listObjectsRequest.setPrefix(StringUtils.removeStart(path, "/"));
				listObjectsRequest.setDelimiter("/");
				ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
				for (String commonPrefix : objectListing.getCommonPrefixes()) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(StringUtils.substringAfterLast(StringUtils.removeEnd(commonPrefix, "/"), "/"));
					fileInfo.setUrl(urlPrefix + "/" + commonPrefix);
					fileInfo.setIsDirectory(true);
					fileInfo.setSize(0L);
					fileInfos.add(fileInfo);
				}
				for (OSSObjectSummary ossObjectSummary : objectListing.getObjectSummaries()) {
					if (ossObjectSummary.getKey().endsWith("/")) {
						continue;
					}
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(StringUtils.substringAfterLast(ossObjectSummary.getKey(), "/"));
					fileInfo.setUrl(urlPrefix + "/" + ossObjectSummary.getKey());
					fileInfo.setIsDirectory(false);
					fileInfo.setSize(ossObjectSummary.getSize());
					fileInfo.setLastModified(ossObjectSummary.getLastModified());
					fileInfos.add(fileInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileInfos;
	}

}
