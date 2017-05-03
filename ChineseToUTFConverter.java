package chinesetoascii;
import org.apache.commons.lang3.StringEscapeUtils;


public class ChineseToUTFConverter
{

    public static void main(String[] args)
    {
        String chineseString = "“移动POS机 交易进行中”错误";

        String unicodeCodes = StringEscapeUtils.escapeJava(chineseString);
        System.out.println(unicodeCodes);
    }
}
