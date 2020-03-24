package blue.endless.wtrader.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;

public class FileIcons {
	private static Image unknownIcon;
	private static HashMap<String, String> extensionToMime;
	private static HashMap<String, Image> icons = new HashMap<>();
	private static String baseAddress = "icons/";
	
	private static void checkExtensions() {
		if (extensionToMime==null) {
			extensionToMime = new HashMap<>();
			
			InputStream in = FileIcons.class.getClassLoader().getResourceAsStream("mime.json");
			try {
				JsonObject obj = Jankson.builder().build().load(in);
				for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
					String value;
					if (entry.getValue() instanceof JsonPrimitive) {
						value = ((JsonPrimitive) entry.getValue()).asString();
					} else {
						value = entry.toString();
					}
					
					extensionToMime.put(entry.getKey(), value);
				}
				
				System.out.println(extensionToMime.size()+" mime types loaded.");
			} catch (IOException | SyntaxError e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static Image getImage(String filename) {
		InputStream imageStream = FileIcons.class.getClassLoader().getResourceAsStream(filename);
		if (imageStream!=null) {
			try {
				Image im = ImageIO.read(imageStream);
				if (im!=null) return im;
				return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
			} catch (IOException e) {}
		}
		return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
	}
	
	private static Image getImage(URL url) {
		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
	private static URL getImageResource(String mimeType) {
		String sanitizedType = mimeType.replace('/', '-');
		//System.out.println("Finding resource for "+sanitizedType);
		
		URL png = FileIcons.class.getClassLoader().getResource(baseAddress+"/"+sanitizedType+".png");
		if (png!=null) return png;
		
		if (sanitizedType.startsWith("application-")) {
			sanitizedType = "application-x-"+sanitizedType.substring("application-".length());
		}
		
		png = FileIcons.class.getClassLoader().getResource(baseAddress+"/"+sanitizedType+".png");
		if (png!=null) return png;
		
		return null;
	}
	
	
	public static Image getUnknownIcon() {
		if (unknownIcon==null) {
			unknownIcon = getImage("icons/application-x-zerosize.png").getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		}
		return unknownIcon;
	}
	
	public static String getMimeType(String extension) {
		checkExtensions();
		String result = extensionToMime.get(extension);
		return (result!=null) ? result : "application-x-zerosize";
	}
	
	public static Image getIcon(String extension) {
		checkExtensions();
		
		String mimeType = extensionToMime.get(extension);
		if (mimeType==null) {
			return getUnknownIcon();
		} else {
			Image icon = icons.get(mimeType);
			if (icon==null) {
				URL rsrc = getImageResource(mimeType);
				if (rsrc==null) return getUnknownIcon();
				Image altIcon = getImage(rsrc).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
				icons.put(mimeType, altIcon);
				return altIcon;
			} else {
				return icon;
			}
		}
	}
}
