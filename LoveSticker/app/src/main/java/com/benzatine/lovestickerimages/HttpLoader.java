package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;


@SuppressWarnings("deprecation")
public class HttpLoader {

	public final int HTTP_CONNECTION_TIMEOUT = 60000;
	public final int HTTP_SO_TIMEOUT = 60000;
	long SIZE_CONSTANT =200000;
	int QUALITY_CONSTANT=70;
	public HttpLoader(Context context){
		ctx=context;
	}
	Context ctx;
	public  HttpLoader(){

	}
	static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
								   boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params,HTTP_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_SO_TIMEOUT);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
			return httpclient;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public String loadDataByPost(String httpUrl, List<NameValuePair> params)
			throws Exception {
		String httpResponseData = null;
		try {
			// request method
			System.out.println("httpUrl::" + httpUrl);
			HttpPost httpPost = new HttpPost(httpUrl);
			// create multiPart entity
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			// MultipartEntity entity = new MultipartEntity(
			// HttpMultipartMode.BROWSER_COMPATIBLE);
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int index = 0; index < params.size(); index++) {
				System.out.println(params.get(index).getName() + " : "
						+ params.get(index).getValue());

				entityBuilder.addTextBody(params.get(index).getName(), params
						.get(index).getValue());
			}

			if (entityBuilder != null) {
				httpPost.setEntity(entityBuilder.build());
			} else {
				System.out.println("entityBuilder is null");
			}
			// Perform the request and check the status code
			HttpResponse httpResponse = getNewHttpClient().execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null
					&& statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........."
						+ "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		// return response
		// note: if there is any error - a null response is sent
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	public String loadDataByPost(String httpUrl, List<NameValuePair> params, List<NameValuePair> paramsImage) throws Exception {
		String httpResponseData = null;
		try {
			// request method
			System.out.println("httpUrl::" + httpUrl);
			HttpPost httpPost = new HttpPost(httpUrl);
			// create multiPart entity
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int index = 0; index < params.size(); index++) {
				System.out.println(params.get(index).getName() + " : " + params.get(index).getValue());
				entityBuilder.addTextBody("" + params.get(index).getName(), "" + params.get(index).getValue());
			}
			// for image
			for (int index = 0; index < paramsImage.size(); index++) {
				System.out.println(paramsImage.get(index).getName() + " : "
						+ paramsImage.get(index).getValue());
				if (paramsImage.get(index).getName().length() > 0 && paramsImage.get(index).getValue().length() > 0) {
					if (paramsImage.get(index).getValue().contains("http://")||paramsImage.get(index).getValue().contains("https://")) {
						// Load image from url
						URL url = new URL(paramsImage.get(index).getValue());
						InputStream in = url.openConnection().getInputStream();
						BufferedInputStream bis = new BufferedInputStream(in,1024 * 8);
						ByteArrayOutputStream out = new ByteArrayOutputStream();

						int len = 0;
						byte[] buffer = new byte[1024];
						while ((len = bis.read(buffer)) != -1) {
							out.write(buffer, 0, len);
						}
						out.close();
						bis.close();

						byte[] data = out.toByteArray();

						ByteArrayBody bab;

                        if (data.length > SIZE_CONSTANT) {
                            try {
                                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
                                data = bytearrayoutputstream.toByteArray();
                                bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
                            }catch (Exception ex){
                                bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
                            }
                        }else {
							System.out.println("data size::" + data.length);
							bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date()));
						}
						entityBuilder.addPart(paramsImage.get(index).getName(),bab);

					}else if((paramsImage.get(index).getValue().contains("assets:"))){
						AssetManager assManager = ctx.getAssets();
						InputStream bis = null;
						try {
							bis = assManager.open(paramsImage.get(index).getValue().substring((paramsImage.get(index).getValue().lastIndexOf(":")+1)));
							ByteArrayOutputStream out=new ByteArrayOutputStream();
							int len = 0;
							byte[] buffer = new byte[1024];
							while ((len = bis.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
							out.close();
							bis.close();

							byte[] data = out.toByteArray();

							ByteArrayBody bab;

                            if (data.length > SIZE_CONSTANT) {
                                try {
                                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
                                    data = bytearrayoutputstream.toByteArray();
                                    bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
                                }catch (Exception ex){
                                    bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
                                }
                            }else {
								System.out.println("data size::" + data.length);
								bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date()));
							}
							entityBuilder.addPart(paramsImage.get(index).getName(),
									bab);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else {
						// load image from local storage
						File binaryFile = new File(paramsImage.get(index).getValue());
						if (binaryFile != null && binaryFile.exists()) {
							System.out.println("file exist");
							FileInputStream is=new FileInputStream(binaryFile);
							BufferedInputStream bis = new BufferedInputStream(is,1024 * 8);
							ByteArrayOutputStream out = new ByteArrayOutputStream();

							int len = 0;
							byte[] buffer = new byte[1024];
							while ((len = bis.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
							out.close();
							bis.close();

							ByteArrayBody bab;

							byte[] data = out.toByteArray();
							Log.e("TAG",""+data.length);
							if(paramsImage.get(index).getValue().toLowerCase().contains("mp4")){
								bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "" + paramsImage.get(index)
										.getValue().substring(paramsImage.get(index).getValue().lastIndexOf(".")));
							}else {
								/*if (data.length >SIZE_CONSTANT) {
									try {
										ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
										Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
										bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
										data = bytearrayoutputstream.toByteArray();
										bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
									}catch (Exception ex){
										bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
									}
								}else{*/
									bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
//								}
							}
							System.out.println("data size::" + data.length);
							//ByteArrayBody bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"."+paramsImage.get(index)
							//		.getValue().substring(paramsImage.get(index).getValue().lastIndexOf(".")));
							//ByteArrayBody bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
							entityBuilder.addPart(paramsImage.get(index).getName(),bab);

							//entityBuilder.addBinaryBody(paramsImage.get(index)
							//		.getName(), binaryFile);

						} else {
							System.out.println("binaryFile is null");
						}
					}
					Log.e("", "loadData : image name--> image_key :" + paramsImage.get(index).getValue());
				}
			}

			if (entityBuilder != null) {
				httpPost.setEntity(entityBuilder.build());
			} else {
				System.out.println("entityBuilder is null");
			}
			// Perform the request and check the status code
			HttpResponse httpResponse = getNewHttpClient().execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........." + "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	public String loadDataByPost2(String httpUrl, List<NameValuePair> params, List<NameValuePair> paramsImage) throws Exception {
		String httpResponseData = null;
		try {
			// request method
			System.out.println("httpUrl::" + httpUrl);
			HttpPost httpPost = new HttpPost(httpUrl);
			// create multiPart entity
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int index = 0; index < params.size(); index++) {
				System.out.println(params.get(index).getName() + " : " + params.get(index).getValue());
				entityBuilder.addTextBody("" + params.get(index).getName(), "" + params.get(index).getValue());
			}
			// for image
			for (int index = 0; index < paramsImage.size(); index++) {
				System.out.println(paramsImage.get(index).getName() + " : "
						+ paramsImage.get(index).getValue());
				if (paramsImage.get(index).getName().length() > 0 && paramsImage.get(index).getValue().length() > 0) {
					if (paramsImage.get(index).getValue().contains("http://") || paramsImage.get(index).getValue().contains("https://")) {
						// Load image from url
						URL url = new URL(paramsImage.get(index).getValue());
						InputStream in = url.openConnection().getInputStream();
						BufferedInputStream bis = new BufferedInputStream(in,1024 * 8);
						ByteArrayOutputStream out = new ByteArrayOutputStream();

						int len = 0;
						byte[] buffer = new byte[1024];
						while ((len = bis.read(buffer)) != -1) {
							out.write(buffer, 0, len);
						}
						out.close();
						bis.close();

						byte[] data = out.toByteArray();

						ByteArrayBody bab;

						if (data.length > SIZE_CONSTANT) {
							try {
								ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
								Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
								bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
								data = bytearrayoutputstream.toByteArray();
								bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
							}catch (Exception ex){
								bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
							}
						}else {
							System.out.println("data size::" + data.length);
							bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date()));
						}
						entityBuilder.addPart(paramsImage.get(index).getName(),bab);

					}else if((paramsImage.get(index).getValue().contains("assets:"))){
						AssetManager assManager = ctx.getAssets();
						InputStream bis = null;
						try {
							bis = assManager.open(paramsImage.get(index).getValue().substring((paramsImage.get(index).getValue().lastIndexOf(":")+1)));
							ByteArrayOutputStream out=new ByteArrayOutputStream();
							int len = 0;
							byte[] buffer = new byte[1024];
							while ((len = bis.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
							out.close();
							bis.close();

							byte[] data = out.toByteArray();

							ByteArrayBody bab;

							if (data.length > SIZE_CONSTANT) {
								try {
									ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
									Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
									bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
									data = bytearrayoutputstream.toByteArray();
									bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
								}catch (Exception ex){
									bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
								}
							}else {
								System.out.println("data size::" + data.length);
								bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date()));
							}
							entityBuilder.addPart(paramsImage.get(index).getName(),
									bab);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else {
						// load image from local storage
						File binaryFile = new File(paramsImage.get(index).getValue());
						if (binaryFile != null && binaryFile.exists()) {
							System.out.println("file exist");
							FileInputStream is=new FileInputStream(binaryFile);
							BufferedInputStream bis = new BufferedInputStream(is,1024 * 8);
							ByteArrayOutputStream out = new ByteArrayOutputStream();

							int len = 0;
							byte[] buffer = new byte[1024];
							while ((len = bis.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
							out.close();
							bis.close();

							ByteArrayBody bab;
							ByteArrayBody bab1;

							byte[] data = out.toByteArray();
							Log.e("TAG",""+data.length);
							if(paramsImage.get(index).getValue().toLowerCase().contains("mp4")){
								bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "" + paramsImage.get(index)
										.getValue().substring(paramsImage.get(index).getValue().lastIndexOf(".")));
							}else {
								if (data.length >SIZE_CONSTANT) {
									try {
										ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
										Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
										bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_CONSTANT, bytearrayoutputstream);
										data = bytearrayoutputstream.toByteArray();
										bab = new ByteArrayBody(data, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".jpg");
									}catch (Exception ex){
										bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
									}
								}else{
									bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
								}
							}
							System.out.println("data size::" + data.length);
							//ByteArrayBody bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"."+paramsImage.get(index)
							//		.getValue().substring(paramsImage.get(index).getValue().lastIndexOf(".")));
							//ByteArrayBody bab = new ByteArrayBody(data,new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".jpg");
							entityBuilder.addPart(paramsImage.get(index).getName(),bab);
							//entityBuilder.addBinaryBody(paramsImage.get(index)
							//		.getName(), binaryFile);

						} else {
							System.out.println("binaryFile is null");
						}
					}
					Log.e("", "loadData : image name--> image_key :" + paramsImage.get(index).getValue());
				}
			}

			if (entityBuilder != null) {
				httpPost.setEntity(entityBuilder.build());
			} else {
				System.out.println("entityBuilder is null");
			}
			// Perform the request and check the status code
			HttpResponse httpResponse = getNewHttpClient().execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........." + "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	public String loadDataByPost1(String httpUrl, List<NameValuePair> params,List<NameValuePair> paramsImage) throws Exception {
		String httpResponseData = null;
		try {
			// request method
			System.out.println("httpUrl::" + httpUrl);
			HttpPost httpPost = new HttpPost(httpUrl);
			// create multiPart entity
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			// MultipartEntity entity = new MultipartEntity(
			// HttpMultipartMode.BROWSER_COMPATIBLE);
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int index = 0; index < params.size(); index++) {
				System.out.println(params.get(index).getName() + " : " + params.get(index).getValue());

				entityBuilder.addTextBody("" + params.get(index).getName(), ""
						+ params.get(index).getValue());
				// }

			}

			for (int index = 0; index < paramsImage.size(); index++) {
				System.out.println(paramsImage.get(index).getName() + " : "
						+ paramsImage.get(index).getValue());
				if (paramsImage.get(index).getName().length() > 0
						&& paramsImage.get(index).getValue().length() > 0) {
					// If the key equals to "image", we use FileBody to
					// transferthe data

					// entity.addPart(params.get(index).getName(), new FileBody(
					// new File(params.get(index).getValue())));
					File binaryFile = new File(paramsImage.get(index)
							.getValue());
					System.out.println("binaryFile::" + binaryFile);
					if (binaryFile != null && binaryFile.exists()) {
						System.out.println("file exist");
						entityBuilder.addBinaryBody(paramsImage.get(index)
								.getName(), binaryFile);
					} else {
						System.out.println("binaryFile is null");
					}

					Log.e("", "loadData : image name--> image_key :"
							+ paramsImage.get(index).getValue());
				}

			}

			// set HTTP request parameters if any
			// if (entity != null) {
			// httpPost.setEntity(entity);
			// }
			if (entityBuilder != null) {
				httpPost.setEntity(entityBuilder.build());
			} else {
				System.out.println("entityBuilder is null");
			}
			// Perform the request and check the status code
			HttpResponse httpResponse = getNewHttpClient().execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null
					&& statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........."
						+ "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());

		}
		// return response
		// note: if there is any error - a null response is sent
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	public String loadBitmapDataByPost(String httpUrl,List<NameValuePair> params, List<NameValuePair> paramsImage)
			throws Exception {
		String httpResponseData = null;
		try {
			// request method
			HttpPost httpPost = new HttpPost(httpUrl);
			// create multiPart entity
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			System.out.println("httpUrl::" + httpUrl);

			// for text parameter

			for (int index = 0; index < params.size(); index++) {
				System.out.println(params.get(index).getName() + " : "
						+ params.get(index).getValue());

				entity.addPart(params.get(index).getName(), new StringBody(
						params.get(index).getValue()));

			}

			// for image

			for (int index = 0; index < paramsImage.size(); index++) {
				System.out.println(paramsImage.get(index).getName() + " : "
						+ paramsImage.get(index).getValue());

				URL url = new URL(paramsImage.get(index).getValue());
				InputStream in = url.openConnection().getInputStream();
				BufferedInputStream bis = new BufferedInputStream(in, 1024 * 8);
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = bis.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				bis.close();

				byte[] data = out.toByteArray();

				System.out.println("data size::" + data.length);
				ByteArrayBody bab = new ByteArrayBody(data, paramsImage.get(
						index).getValue()
						+ ".png");
				entity.addPart("" + paramsImage.get(index).getName(), bab);

				Log.e("", "loadData image=" + paramsImage.get(index).getName()
						+ " : " + paramsImage.get(index).getValue());

			}

			// set HTTP request parameters if any
			if (entity != null) {
				httpPost.setEntity(entity);
			}
			// Perform the request and check the status code
			HttpResponse httpResponse = getNewHttpClient().execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null
					&& statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........."
						+ "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		// return response
		// note: if there is any error - a null response is sent
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	/**
	 * This method will only handle errors that can occur at the network or HTTP
	 * level. application specific errors that are sent in XML/JSON payLoad
	 * should be handled by the activity calling this http loader.
	 * 
	 * @param httpUrl
	 * @return http response from server
	 * @throws Exception
	 */
	public String loadDataByGet(String httpUrl) throws Exception {
		String httpResponseData = null;
		try {
			httpUrl = httpUrl.replace(" ", "%20");
			// request method
			HttpGet httpGet = new HttpGet(httpUrl);

			System.out.println("httpUrl::" + httpUrl);

			HttpResponse httpResponse = getNewHttpClient().execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null
					&& statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseData = EntityUtils.toString(httpEntity);
			} else {
				System.out.println("ERROR........."
						+ "statusLine null or result not ok");
			}
		} catch (Exception ex) {
			System.out.println("ERROR........." + "connection error.");
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		// return response
		// note: if there is any error - a null response is sent
		System.out.println("httpResponseData:::" + httpResponseData);
		return httpResponseData;
	}

	public static Bitmap getBitmapFromURL(String src) {
		System.out.println("photo src::" + src);
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap getImageBitmapFromUrl(URL url) {
		Bitmap bm = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				return bm;
			}
			conn.connect();
			InputStream is = conn.getInputStream();

			BufferedInputStream bis = new BufferedInputStream(is);
			try {
				bm = BitmapFactory.decodeStream(bis);
			} catch (OutOfMemoryError ex) {
				bm = null;
			}
			bis.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bm;
	}

}
