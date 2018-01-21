package wapio.convert;

public class Main {
	public static void main(String[] args) {
		if (args == null | args.length <= 0) { System.out.println("ERROR ファイルパスを入力してください。"); return; }
		FileTypeConverter converter = new FileTypeConverter();
		@SuppressWarnings("static-access")
		String xmlJson = converter.convertJsonToXml(args[0]);
		System.out.println(xmlJson);
	}
}
