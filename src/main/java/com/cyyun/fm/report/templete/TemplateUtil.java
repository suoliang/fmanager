package com.cyyun.fm.report.templete;

/**
 * <h3></h3>
 * <p></p>
 * @author GUOQIANG
 * @version 1.0.0
 */
public class TemplateUtil {

	public static String getValueAfterRepalceSpecialWord(String str) {
		if (str != null) {
			return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;").replaceAll("\\n", "");
			//					.replaceAll("〣", "").replaceAll("&#55357;&#56397;", "")		
			//					.replaceAll("\\u0000\\n", "").replaceAll("\\u0000", "")		
			//					.replaceAll("&#55357;", "").replaceAll("&#56911;", "")	
			//					.replaceAll("&", "&amp;").replaceAll("&#58635;", "")			
			//					.replaceAll("&#55395;", "").replaceAll("&#56382;", "");			
		} else {
			return "";
		}
	}

	/**
	 * 字符串长度检查    用于导出Excel （Excel的一个单元格最大承受32767个字符）
	 * @param str
	 * @return
	 */
	public static String checkSize(String str) {
		if (str != null) {
			if (str.length() > 32000) {
				str = str.substring(0, 32000) + "...";
			}
			return str;
		} else {
			return "";
		}
	}

	private static String TAGS = "<w:p>";
	private static String TAGS_BEGIN = "<w:p wsp:rsidR=\"008C1D22\" wsp:rsidRDefault=\"008C1D22\" wsp:rsidP=\"008C1D22\"><w:pPr><w:ind w:first-line=\"420\" /><w:jc w:val=\"left\" /></w:pPr><w:r><w:rPr><w:rFonts w:hint=\"fareast\" /><wx:font wx:val=\"宋体\" /></w:rPr><w:t>";
	private static String TAGS_END = "</w:t></w:r></w:p>";

	public static String getValueAfterRepalceTNR(String str) {
		if (str != null) {
			str = str.replaceAll("[\\t\\n\\r]", TAGS);
			str = str.replaceAll("\\s+", "").replaceAll("[　]+", "");
			str = str.replaceAll("[\\t]", "");
			str = str.replaceAll(TAGS + TAGS + TAGS + TAGS + TAGS + TAGS + TAGS + TAGS, TAGS + TAGS);
			str = str.replaceAll(TAGS + TAGS + TAGS + TAGS + TAGS + TAGS, TAGS + TAGS);
			str = str.replaceAll(TAGS + TAGS + TAGS + TAGS, TAGS + TAGS);
			str = str.replaceAll(TAGS + TAGS, TAGS);

			str = str.replace(TAGS, TAGS_END + TAGS_BEGIN);

			return (TAGS_BEGIN + str + TAGS_END).replace(TAGS_BEGIN + TAGS_END, "");
		} else {
			return "";
		}
	}

}
