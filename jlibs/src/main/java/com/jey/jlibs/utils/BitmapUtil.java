package com.jey.jlibs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class BitmapUtil {

	private static final Size ZERO_SIZE = new Size(0, 0);
	private static final Options OPTIONS_GET_SIZE = new Options();
	private static final Options OPTIONS_DECODE = new Options();
	private static final byte[] LOCKED = new byte[0];
	private static int count=0;

	private static final LinkedList<String> CACHE_ENTRIES = new LinkedList<String>();

	private static final Queue TASK_QUEUE = new LinkedList<Object>();


	private static final Set TASK_QUEUE_INDEX = new HashSet<Object>();

	private static final Map<String, WeakReference<Bitmap>> IMG_CACHE_INDEX = new HashMap<String, WeakReference<Bitmap>>(); // ͨ��ͼƬ·��,ͼƬ��С

	private static int CACHE_SIZE = 50;

	static {
		OPTIONS_GET_SIZE.inJustDecodeBounds = true;
		new Thread() {
			{
				setDaemon(true);
			}

			public void run() {
				while (true) {
					synchronized (TASK_QUEUE) {
						if (TASK_QUEUE.isEmpty()) {
							try {
								TASK_QUEUE.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					QueueEntry entry = (QueueEntry) TASK_QUEUE.poll();
					String key = createKey(entry.path, entry.width,
							entry.height);
					TASK_QUEUE_INDEX.remove(key);
					try {
						createBitmap(entry.path,entry.width, entry.height);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public static Bitmap getBitmap(String path, int width, int height) {
		if (path == null) {
			return null;
		}
		Bitmap bitMap = null;
		try {
//			if (IMG_CACHE_INDEX.size() >= CACHE_SIZE) {
//				destoryLast();
//			}
			bitMap = useBitmap(path, width, height);
			if (CACHE_ENTRIES.size() > IMG_CACHE_INDEX.size()) {
				for (int i = CACHE_ENTRIES.size() - 1; i >= 0; i--) {
					String key = (String) CACHE_ENTRIES.get(i);
					if (key.length() > 0) {
						WeakReference<Bitmap> weak = IMG_CACHE_INDEX.get(key);
						if (weak == null)
							CACHE_ENTRIES.remove(key);
					}
				}
			}
			if (bitMap != null && !bitMap.isRecycled()) {
				return bitMap;
			}
			try {
				bitMap = createBitmap(path, width, height);
				count=0;
			} catch (Exception e) {
				new File(path).delete();
				e.printStackTrace();
			}
		} catch (OutOfMemoryError err) {
			if(count<10&&destoryLast()){
				count++;
				return getBitmap(path, width, height);
			}
		}
		return bitMap;
	}

	public static Size getBitMapSize(String path) {
		File file = new File(path);
		if (file.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				BitmapFactory.decodeStream(in, null, OPTIONS_GET_SIZE);
				return new Size(OPTIONS_GET_SIZE.outWidth,
						OPTIONS_GET_SIZE.outHeight);
			} catch (FileNotFoundException e) {
				return ZERO_SIZE;
			} finally {
				closeInputStream(in);
			}
		}
		return ZERO_SIZE;
	}

	private static Bitmap useBitmap(String path, int width, int height) {
		Bitmap bitMap = null;
		String key = createKey(path, width, height);
		if(IMG_CACHE_INDEX.containsKey(key)){
			synchronized (LOCKED) {
				bitMap = (Bitmap) IMG_CACHE_INDEX.get(key).get();
				if (null != bitMap) {
					if (CACHE_ENTRIES.remove(key)) {
						CACHE_ENTRIES.addFirst(key);
					}
				}else CACHE_ENTRIES.remove(key);
			}
		}
		return bitMap;
	}
	public static void destoryAll() {
		synchronized (LOCKED) {
			while(!CACHE_ENTRIES.isEmpty()){
				String key = (String) CACHE_ENTRIES.removeLast();
				if (key.length() > 0) {
					WeakReference<Bitmap> weak=IMG_CACHE_INDEX.remove(key);
					if(weak==null)return;
					Bitmap bitMap = (Bitmap) weak.get();
					if (bitMap != null && !bitMap.isRecycled()) {
						bitMap.recycle();
						bitMap = null;
					}
				}
			}
		}
	}
	// �������һ��ͼƬ
	public static Boolean destoryLast() {
		try{
			synchronized (LOCKED) {
				if(CACHE_ENTRIES.size()==0)return false;
				String key = (String) CACHE_ENTRIES.removeLast();
				if (key.length() > 0) {
					WeakReference<Bitmap> weak=IMG_CACHE_INDEX.remove(key);
					if(weak==null)return destoryLast();
					Bitmap bitMap = (Bitmap) weak.get();
					if (bitMap != null && !bitMap.isRecycled()) {
						bitMap.recycle();
						bitMap = null;
					}
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	private static String createKey(String path, int width, int height) {
		if (null == path || path.length() == 0) {
			return "";
		}
		return path + "_" + width + "_" + height;
	}

	public static Bitmap createBitmap(String path,int width, int height) throws Exception {
		File file = new File(path);
		if (file.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				Size size = getBitMapSize(path);
				if (size.equals(ZERO_SIZE)) {
					return null;
				}
				int scale = 1;
				int x=0,y=0;
				if(width<=0&&height<=0){
					width=size.width;
					height=size.height;
				}else if(width>0&&height>0){
					if(width/height>size.width/size.height){
						int h=width* size.height / size.width;
						scale=size.height / h;
						//y=(bitMap.getHeight()-height)/2;
					}else{
						int w=height * size.width/ size.height;
						scale=size.width / w;
						//x=(bitMap.getHeight()-width)/2;
					}
				}else{
					if(width>0){
						height=width* size.height / size.width;
					}else{
						width=height * size.width/ size.height;
					}
					int a = size.getWidth() / width;
					int b = size.getHeight() / height;
					scale = Math.max(a, b);					
				}
				synchronized (OPTIONS_DECODE) {
					OPTIONS_DECODE.inSampleSize = scale;
					Bitmap bitMap = BitmapFactory.decodeStream(in, null,
							OPTIONS_DECODE);
					float sx=1,sy=1;
					Matrix m=new Matrix();
					int w=bitMap.getWidth();
					int h=bitMap.getHeight();
					if(width<w)sx=(float)width/(float)w;
					if(height<h)sy=(float)height/(float)h;
					if(width*h>w*height){
						height=w*height/width;
						y=(h-height)/2;
						h=h-y*2;
						sy=sx;
					}else{
						width=h*width/height;
						x=(w-width)/2;
						w=w-x*2;
						sx=sy;
					}
					m.setScale(sx, sy);
					bitMap=Bitmap.createBitmap(bitMap, x, y, w, h, m,false);
					String key = createKey(path, width, height);
					synchronized (LOCKED) {
						IMG_CACHE_INDEX.put(key, new WeakReference<Bitmap>(bitMap));
						if(CACHE_ENTRIES.contains(key))CACHE_ENTRIES.remove(key);
						CACHE_ENTRIES.addFirst(key);
					}
					return bitMap;
				}
			} catch (Exception e) {
				throw e;
			} finally {
				closeInputStream(in);
			}
		}
		return null;
	}
	public static Size getBitMapSize(InputStream in) {
		BitmapFactory.decodeStream(in, null, OPTIONS_GET_SIZE);
		return new Size(OPTIONS_GET_SIZE.outWidth,
				OPTIONS_GET_SIZE.outHeight);
	}
	public static Bitmap createBitmap(InputStream in,int width, int height) {
		try {
			Size size = getBitMapSize(in);
			if (size.equals(ZERO_SIZE)) {
				return null;
			}
			int scale = 1;
			int x=0,y=0;
			if(width<=0&&height<=0){
				width=size.width;
				height=size.height;
			}else if(width>0&&height>0){
				if(width/height>size.width/size.height){
					int h=width* size.height / size.width;
					scale=size.height / h;
					//y=(bitMap.getHeight()-height)/2;
				}else{
					int w=height * size.width/ size.height;
					scale=size.width / w;
					//x=(bitMap.getHeight()-width)/2;
				}
			}else{
				if(width>0){
					height=width* size.height / size.width;
				}else{
					width=height * size.width/ size.height;
				}
				int a = size.getWidth() / width;
				int b = size.getHeight() / height;
				scale = Math.max(a, b);					
			}
			synchronized (OPTIONS_DECODE) {
				OPTIONS_DECODE.inSampleSize = scale;
				Bitmap bitMap = BitmapFactory.decodeStream(in, null,
						OPTIONS_DECODE);
				float sx=1,sy=1;
				Matrix m=new Matrix();
				int w=bitMap.getWidth();
				int h=bitMap.getHeight();
				if(width<w)sx=(float)width/(float)w;
				if(height<h)sy=(float)height/(float)h;
				if(width*h>w*height){
					height=w*height/width;
					y=(h-height)/2;
					h=h-y*2;
					sy=sx;
				}else{
					width=h*width/height;
					x=(w-width)/2;
					w=w-x*2;
					sx=sy;
				}
				m.setScale(sx, sy);
				bitMap=Bitmap.createBitmap(bitMap, x, y, w, h, m,false);
				return bitMap;
			}
		} finally {
			closeInputStream(in);
		}
	}

	private static void closeInputStream(InputStream in) {
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				Log.v("BitMapUtil", "closeInputStream==" + e.toString());
			}
		}
	}

	static class Size {
		private int width, height;

		Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	static class QueueEntry {
		public String path;
		public int width;
		public int height;
	}


}
