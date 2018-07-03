package com.lcz.service.house;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import com.lcz.ApplicationTests;

public class QiNiuServiceTests extends ApplicationTests {
	 @Autowired
    private IQiNiuService qiNiuService;

    @Test
    public void testUploadFile() {
        String fileName = "‪D:\\lufei.jpg";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());

        try {
            Response response = qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDelete() {
    	String key="FjH7nqun1bimA1qSLFaAT1D7PCOk";
    	try {
			Response response = qiNiuService.delete(key);
			Assert.assertTrue(response.isOK());
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
