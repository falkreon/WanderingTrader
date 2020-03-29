package blue.endless.wtrader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nullable;

import com.google.common.io.ByteStreams;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

public class ZipAccess {
	public static boolean hasFile(File f, String filename) {
		try( ZipInputStream in = new ZipInputStream(new FileInputStream(f)) ) {
			
			ZipEntry cur = in.getNextEntry();
			while (cur!=null) {
				if (cur.getName().equals(filename)) return true;
				
				cur = in.getNextEntry();
			}
			
			return false;
		} catch (IOException exception) {
			return false;
		}
	}
	
	@Nullable
	public static String findFile(File f, String suffix) {
		try( ZipInputStream in = new ZipInputStream(new FileInputStream(f)) ) {
			
			ZipEntry cur = in.getNextEntry();
			while (cur!=null) {
				if (cur.getName().endsWith(suffix)) return cur.getName();
				
				cur = in.getNextEntry();
			}
			
			return null;
		} catch (IOException exception) {
			return null;
		}
	}
	
	@Nullable
	public static byte[] getFile(File f, String filename) {
		try( ZipInputStream in = new ZipInputStream(new FileInputStream(f)) ) {
			
			ZipEntry cur = in.getNextEntry();
			while (cur!=null) {
				if (cur.getName().equals(filename)) {
					long sz = cur.getSize();
					if (sz>Integer.MAX_VALUE) throw new IOException("Can only read up to 2GB of data into a byte[].");
					if (sz==-1) {
						return ByteStreams.toByteArray(in);
					} else {
						byte[] result = new byte[(int)sz];
						ByteStreams.readFully(in, result);
						return result;
					}
				}
				
				cur = in.getNextEntry();
			}
			
			return null;
		} catch (IOException exception) {
			return null;
		}
	}
	
	@Nullable
	public static JsonObject getJsonFile(File f, String filename) {
		try( ZipInputStream in = new ZipInputStream(new FileInputStream(f)) ) {
			
			ZipEntry cur = in.getNextEntry();
			while (cur!=null) {
				if (cur.getName().equals(filename)) {
					JsonObject obj = Jankson.builder().build().load(in);
					return obj;
				}
				
				cur = in.getNextEntry();
			}
			
			return null;
		} catch (IOException | SyntaxError exception) {
			return null;
		}
	}
}
