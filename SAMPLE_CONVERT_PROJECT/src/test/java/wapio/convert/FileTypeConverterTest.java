package wapio.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileTypeConverterTest {

	private FileTypeConverter converter;
	
	private static final String RESOURCES_FILDER_PATH = "/Users/ookawarakeita/Documents/workspacew/SAMPLE_CONVERT_PROJECT/src/test/resources/wapio/convert/";
	
	@Before
	public void init() {
		converter = new FileTypeConverter();
	}
	
	@After
	public void tear() {
		converter = null;
	}
	
	@Test
	public void test_convertJsonToXml_正常系() {
		String actual = converter.convertJsonToXml(RESOURCES_FILDER_PATH + "data.json");
		String expected = getFileData(RESOURCES_FILDER_PATH + "data.json");
		assertThat(actual, is(expected));
	}

	private String getFileData(String filepath) {
		if (filepath == null | filepath.equals("")) { System.out.println("ERROR ファイルパスが設定されていません。"); return null; }
		File f = new File(filepath);
		if (!f.exists() & !f.isFile()) { System.out.println("ERROR 有効なファイルパスではありません。"); return null; }
		String filedata = null;
		try (FileInputStream input = new FileInputStream(f)) {
			byte[] buf = new byte[new Long(f.length()).intValue()];
			input.read(buf);
			if (buf.length <= 0) { System.out.println("INFO ファイルデータのバッファが0byteです。"); }
			filedata = new String(buf, "UTF-8");
		} catch (IOException e){
			e.printStackTrace();
		}
		return filedata;
	}
}
