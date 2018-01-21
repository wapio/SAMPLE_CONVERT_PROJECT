package wapio.convert;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FileTypeConverter {

	public static String convertJsonToXml(String filepath) {
		System.out.println("INFO JSON->XML変換処理を開始します。");
		String stringJson = getStringFileData(filepath);
		if (stringJson == null | stringJson.equals("")) { System.out.println("ERROR JSON→XML変換処理を終了します。"); return null; }
		Map<String, Object> mapJson = null;
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			Object objectJson = scriptEngine.eval(String.format("(%s)", stringJson));
			mapJson = convertObjectJsonToMap(objectJson);
		} catch (ScriptException e) {
			System.out.println("ERROR スクリプトの実行で異常終了しました。処理を終了します。");
			e.printStackTrace();
		}
		outputMapJsonToXmlFile(mapJson, filepath);
		return mapJson.toString();
	}
	
	private static void outputMapJsonToXmlFile(Map<String, Object> mapJson, String filepath) {
		String outputFilePath = filepath.replaceAll("\\.json$", ".xml");
		System.out.println("INFO JSON->XMLファイルを出力します。");
		System.out.println("INFO ファイル出力先 -> " + outputFilePath);
		try (XMLEncoder xmlEncorder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(outputFilePath)))) {
			xmlEncorder.writeObject(mapJson);
		} catch (Exception e) {
			System.out.println("ERROR 出力用XMLファイルができませんでした。処理を終了します。");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> convertObjectJsonToMap(Object objectJson) {
		Map<String, Object> mapJson = new LinkedHashMap<String, Object>();
		try {
			Object[] keysOfJson = ((java.util.Set<Object>)objectJson.getClass().getMethod("keySet").invoke(objectJson)).toArray();
			if (keysOfJson == null | keysOfJson.length <= 0) { System.out.println("ERROR Jsonデータのプロパティ名配列の取得に失敗しました。処理を終了します。"); return null; }
			Method methodToGetValueOfJson = objectJson.getClass().getMethod("get", Class.forName("java.lang.Object"));
			for (Object key : keysOfJson) {
				Object value = methodToGetValueOfJson.invoke(objectJson, key);
				Class<?> javaScriptClass = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
				if (javaScriptClass.isInstance(value)) {
					mapJson.put(key.toString(), convertObjectJsonToMap(value));
				} else {
					mapJson.put(key.toString(), methodToGetValueOfJson.invoke(objectJson, key));
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR JavaScriptのクラスが見つかりませんでした。処理を終了します。");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("ERROR メソッドが見つかりませんでした。処理を終了します。");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR 処理の異常終了を検知しました。エラー内容を確認してください。");
			e.printStackTrace();
		}	
		return mapJson;
	}
	
	private static String getStringFileData(String filepath) {
		File f = new File(filepath);
		if (!f.exists() | !f.isFile()) { System.out.println("ERROR 有効なファイルパスが指定されていません。"); return null; }
		String stringJson = null;
		try (FileInputStream input = new FileInputStream(f)) {
			byte[] buf = new byte[new Long(f.length()).intValue()];
			input.read(buf);
			if (buf.length <= 0) { System.out.println("ERROR JSONファイルデータの取得ができませんでした。"); return null; }
			stringJson = new String(buf, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringJson;
	}
}