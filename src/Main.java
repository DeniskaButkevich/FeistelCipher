import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {

    static int rounds = 16;
    static int shiftKey = 2;
    static int sizeOfChar = 8;
    static int sizeOfBlock = 128;
    static int lengthKey = 64;
    static String key = "Pizda";
    static String criptStr = "Da poshel ya na hui";
    static String[] Blocks; //сами блоки в двоичном формате


    public static void main(String[] args) throws UnsupportedEncodingException {

        Scanner scan = new Scanner(System.in);
        int number = scan.nextInt();

        System.out.println("key: " + key);
        System.out.println("criptStr: " + criptStr);

        key = StringFromNormalFormatToBinary(key);
        key = CorrectKeyWord(key);
        criptStr = StringToRightLength(criptStr);

        String binCriptStr = StringFromNormalFormatToBinary(criptStr);
        System.out.println("bin_cript_str: " + binCriptStr);

        String encodeStr = criptFeist(binCriptStr);
        System.out.println("encode_str: " + encodeStr);

        String decodStr= decriptFeist(encodeStr);
        System.out.println("decode_str: " + decodStr);

        String normalFormatDecodeStr = StringFromBinaryToNormalFormat(decodStr);
        System.out.println("normalFormatDecodeStr: " + normalFormatDecodeStr);
    }

    static String feist(String binString, boolean reverse) throws UnsupportedEncodingException {

        CutBinaryStringIntoBlocks(binString);

        for (int i = 0; i < rounds; i++) {

            for (int j = 0; j < Blocks.length; j++){

                if (reverse) {
                    //расшифровка DES один раунд
                    String L = Blocks[j].substring(0, Blocks[j].length() / 2);
                    String R = Blocks[j].substring(Blocks[j].length() / 2, Blocks[j].length() );

                    Blocks[j] = XOReString(f(L, key), R) + L;

                } else {
                    //шифрование DES один раунд
                    String L = Blocks[j].substring(0, Blocks[j].length() / 2);
                    String R = Blocks[j].substring(Blocks[j].length() / 2, Blocks[j].length() );

                    Blocks[j] = R + XOReString(L, f(R, key));
                }

            }
            if (reverse) {
                key = KeyToNextRound(key);
            }else if( i!=15) {
                key = KeyToPrevRound(key);
            }
        }

        binString = "";
        for (int i = 0; i < Blocks.length; i++){
            binString += Blocks[i];
        }
        return binString;
    }

    //своя фннкция F для шифра
    static String f(String str, String key) {

        String leftPartStr = key.substring(0, key.length() / 2);
        String rigtPartStr = key.substring(key.length() / 2, key.length());
        String newKey = rigtPartStr + leftPartStr;

        return XOReString(str, newKey);
    }

    //Перевод символов строки в байт код, а байт код в двоичный код.
    static String StringFromNormalFormatToBinary(String str) {

        String output = "";
        byte[] bytes = str.getBytes();

        for (byte b : bytes) {
            int val = b;
            String binary = "";
            for (int i = 0; i < 8; i++) {
                binary += (val & 128) == 0 ? 0 : 1;
                val <<= 1;
            }
            output += binary;
        }
        return output;
    }

    //Перевод двоичного когда в байт код, а затем байт воды в символы
    static String StringFromBinaryToNormalFormat(String str) {

        String output = "";

        while (str.length() > 0) {
            String char_binary = str.substring(0, sizeOfChar );
            str = str.substring(sizeOfChar);

            int a = 0;
            int degree = char_binary.length() - 1;

            for (String c : char_binary.split("(?!^)"))
                a += Integer.parseInt(c) * (int) Math.pow(2, degree--);

            output += ((char) a);
        }
        return output;
    }

    //Вызов сети фейстеля с параметром шифрования
    static String criptFeist(String str) throws UnsupportedEncodingException {
        return feist(str, false);
    }

    //Вызов сети фейстеля с параметром расшифрования
    static String decriptFeist(String str) throws UnsupportedEncodingException {
        return feist(str, true);
    }

    //XOR строк с двоичным кодом
    static String XOReString(String strOne, String strTwo) {

        String result = new String();
        for (int i = 0; i < strOne.length(); i++) {
            if (strOne.substring(i, i + 1).equals(strTwo.substring(i, i + 1))) {
                result += "0";
            } else result += "1";
        }
        return result;
    }

    //Вычисление ключа для следующего раунда шифрования DES. Циклический сдвиг >> shiftKey
    static String KeyToNextRound(String key) {
        for (int i = 0; i < shiftKey; i++) {
            key = key.substring(key.length() - 1) + key;
            key = key.substring(0, key.length() - 1);
        }

        return key;
    }

    //Вычисление ключа для следующего раунда расшифровки DES. циклический сдвиг << shiftKey.
    static String KeyToPrevRound(String key) {
        for (int i = 0; i < shiftKey; i++) {
            key = key + key.substring(0, 1);
            key = key.substring(1, key.length());
        }

        return key;
    }

    //доводим длину ключа до нужной
    static String CorrectKeyWord(String str)
    {
        if (str.length() > lengthKey)
            str = str.substring(0, lengthKey);
        else
            while (str.length() < lengthKey)
                str = "0" + str;

        return str;
    }

    //доводим строку до размера, чтобы делилась на sizeOfBlock
    static String StringToRightLength(String str)
    {
        while (((str.length() * sizeOfChar) % sizeOfBlock) != 0)
            str += "#";

        return str;
    }

    //разбиение двоичной строки на блоки
    static void CutBinaryStringIntoBlocks(String str)
    {
        Blocks = new String[str.length() / sizeOfBlock];

        int lengthOfBlock = str.length() / Blocks.length;

        for (int i = 0; i < Blocks.length; i++)
            Blocks[i] = str.substring(i*lengthOfBlock, lengthOfBlock*(i+1));
    }
}