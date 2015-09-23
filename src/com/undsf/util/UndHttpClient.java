package com.undsf.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arathi on 2015/04/10.
 */
public class UndHttpClient {
    Log logger = LogFactory.getLog(UndHttpClient.class);

    public static UndHttpClient instance = null;
    public static final String DEFAULT_PROXY_IP = "127.0.0.1";
    public static final int DEFAULT_PROXY_PORT = 8087;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static Pattern CharsetPattern = Pattern.compile("charset=([0-9A-Za-z\\-]+)");

    protected CloseableHttpClient client;
    protected HttpHost proxy = null;
    protected RequestConfig config = null;
    protected String defaultCharset = DEFAULT_CHARSET;
    protected boolean findCharset = false;

    public static UndHttpClient getInstance() {
        if (instance == null){
            instance = new UndHttpClient();
        }
        return instance;
    }

    public static void initInstance(){
        instance = new UndHttpClient();
    }

    public static void initInstance(boolean useProxy){
        instance = new UndHttpClient(useProxy);
    }

    public static void initInstance(String proxyIp, int proxyPort){
        instance = new UndHttpClient(proxyIp, proxyPort);
    }

    public UndHttpClient(){
        initClient();
        Properties prop = new PropertiesUTF8();
        try {
            prop.load("!/httpclient.ini");
            boolean useProxy = Boolean.parseBoolean(prop.getProperty("proxy.enable", "false"));
            String proxyIp = prop.getProperty("proxy.ip", DEFAULT_PROXY_IP);
            int proxyPort = Integer.parseInt(prop.getProperty("proxy.port", DEFAULT_PROXY_PORT+""));
            defaultCharset = prop.getProperty("charset.default", DEFAULT_CHARSET);
            findCharset = Boolean.parseBoolean(prop.getProperty("charset.find", "false"));
            if (useProxy) {
                initProxy(proxyIp, proxyPort);
            }
        }
        catch (IOException e){
            logger.warn("配置文件httpclient.ini无法读取！跳过代理服务器等设置。");
        }
    }

    public UndHttpClient(boolean useProxy){
        initClient();
        if (useProxy){
            initProxy(DEFAULT_PROXY_IP, DEFAULT_PROXY_PORT);
        }
    }

    public UndHttpClient(String proxyIp, int proxyPort){
        initClient();
        initProxy(proxyIp, proxyPort);
    }

    public void initClient(){
        client = HttpClients.createDefault();
    }

    public void initProxy(String proxyIp, int proxyPort){
        proxy = new HttpHost(proxyIp, proxyPort, "http");
        config = RequestConfig.custom().setProxy(proxy).build();
    }

    public String getRequest(String url) throws IOException{
        String html = "";
        CloseableHttpResponse response = null;
        try{
            HttpGet request = new HttpGet(url);
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码："+response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            //编码转换的实际情况要根据网页编码适配
            Header header = entity.getContentEncoding();
            String encoding = header!=null ? header.getName() : null;
            //EntityUtils.toString(entity);
            byte[] messageBody = EntityUtils.toByteArray(entity);
            if (findCharset && encoding == null){
                //分析内容查找编码
                html = new String(messageBody);
                Matcher matcher = CharsetPattern.matcher(html);
                if (matcher.find()){
                    encoding = matcher.group(1);
                }
            }
            if (encoding == null){
                encoding = defaultCharset;
            }
            //html = EntityUtils.toString(entity, encoding);
            html = new String(messageBody, encoding);
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return html;
    }

    public String getRequest(String url, PairList<String,String> params) throws IOException{
        String urlWithParam = url;
        if (params!=null && params.size()>0){
            urlWithParam = (url.indexOf("?")>=0)?url:(url+"?");
            boolean firstParamFlag = true;
            for (Pair<String,String> param : params){
                if (!firstParamFlag){
                    urlWithParam += "&";
                }
                else{
                    firstParamFlag = false;
                }
                String key = param.getKey();
                String value = param.getValue();
                urlWithParam += key+"="+value;
            }
        }
        return getRequest(urlWithParam);
    }

    public String postRequest(String url, PairList<String, String> params) throws IOException{
        String html = "";
        CloseableHttpResponse response = null;
        try{
            HttpPost request = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            //组装nvps
            if (params != null && params.size()>0){
                for (Pair<String, String> param : params){
                    nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            html = EntityUtils.toString(entity, "UTF-8");
            response.close();
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return html;
    }

    public String requestWithCache(String url, String path, String method, PairList<String, String> params) throws IOException{
        String html = "";
        //先检查cache是否存在
        StringFileReader cacheFileReader = null;
        boolean cacheExists = false;
        try {
            cacheFileReader = new StringFileReader(path, "UTF-8");
            cacheExists = true;
        }
        catch (IOException e){
            cacheExists = false;
        }
        //如果不存在，联网
        if (!cacheExists){
            String METHOD = method.toUpperCase();
            if (METHOD.equals(METHOD_GET)){
                html = getRequest(url, params);
            }
            else if (METHOD.equals(METHOD_POST)){
                html = postRequest(url, params);
            }
            //获取到html后，写入文件
            try {
                StringFileWriter writer = new StringFileWriter(path, "UTF-8");
                writer.write(html);
                writer.flush();
                writer.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            html = cacheFileReader.readAll();
        }
        //fileReader.close();
        return html;
    }

    public boolean download(String path, String url, boolean omissionFileName, boolean overwrite) throws IOException{
        CloseableHttpResponse response = null;
        try{
            String parent = "";
            String fileFullPath = "";
            if (omissionFileName){
                int indexOfSlash = url.lastIndexOf('/');
                String fileName = url;
                if (indexOfSlash>=0){
                    fileName = url.substring(indexOfSlash);
                }
                parent = path;
                fileFullPath = parent + Constants.DIR_SEPARATOR + fileName;
            }
            else{
                int indexOfSlash = path.lastIndexOf(Constants.DIR_SEPARATOR);
                parent = path.substring(0, indexOfSlash);
                fileFullPath = path;
            }

            File file = new File(fileFullPath);
            if (file.exists()){
                //System.err.println(fileFullPath+"已存在！");
                return false;
            }
            System.out.println("开始下载："+url+" 到 "+fileFullPath);

            //建立目录
            File dir = new File(parent);
            if (!dir.exists()){
                if ( dir.mkdirs()==false ){
                    return false;
                }
            }
            HttpGet request = new HttpGet(url);
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            FileOutputStream fos = new FileOutputStream(fileFullPath);
            entity.writeTo(fos);
            fos.close();
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return false;
    }

    public boolean close(){
        try{
            client.close();
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

}
