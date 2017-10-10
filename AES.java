import java.io.*;
import java.util.*;
import java.security.Key;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	Key AESKEY;
	
	// 키값을 지정해주는 생성자
	public AES(Key key) {
		this.AESKEY = key;
	}
	
	/*  -> 파일 읽어와 암호화를  해서 다시 파일로 저장하는 함수
	 *   originalFile : 원본 파일 (암호화할 대상 파일)
	 *   cipherFilename : 암호화한 파일의 이름 (이 이름으로 암호화 파일을 만든다)
	 */
	public void Encrypt(File originalFile, String cipherFilename) throws Exception {
		System.out.println("파일을 암호화합니다.");
		
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");   // getInstance(알고리즘 이름, 모드, 패딩)
	    aesCipher.init(Cipher.ENCRYPT_MODE, AESKEY);
	    
	    BufferedReader inputFile = new BufferedReader(new FileReader(originalFile));   // 읽어들일 대상 파일
	    FileOutputStream outputFile = new FileOutputStream(cipherFilename);   // 암호화되어 만들어질 파일
	    
	    // -- 파일을 라인 단위로 읽어 암호화하여 저장하는 부분 -- //
	    while(true) {
	    	String line = inputFile.readLine();
	    	
	    	if (line == null)
	    		break;
	    	
	    	byte[] plaintext = line.getBytes("UTF8");
	    	byte[] ciphertext = aesCipher.doFinal(plaintext);
	    	outputFile.write(ciphertext);
	    }
	    
	    inputFile.close();
	    outputFile.close();
		
		System.out.println("암호화된 파일은 " + cipherFilename + " 입니다.");
	}
	
	/*  -> 암호화된 파일 읽어와 복호화를  해서 다시 원본 파일로 저장하는 함수
	 *   cipherFile : 암호화된 파일 (복호화할 대상 파일)
	 *   originalFilename : 복호화한 파일의 이름 (이 이름으로 복호화 파일을 만든다)
	 */
	public void Decrypt(File cipherFile, String originalFilename) throws Exception {
		System.out.println("파일을 복호화합니다.");
		
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");   // getInstance(알고리즘 이름, 모드, 패딩)
	    aesCipher.init(Cipher.DECRYPT_MODE, AESKEY);
	    
	    FileInputStream inputFile = new FileInputStream(cipherFile);   // 읽어들일 암호화 파일
	    PrintWriter outputFile = new PrintWriter(originalFilename);   // 복호화하여 만들어질 파일
	    
	    // -- 파일을 byte로 읽어와 복호화하여 문자열로 변환하여 저장하는 부분 -- //
	    int read;
	    byte[] text = new byte[1024]; 
	    while((read = inputFile.read(text)) != -1) {
	    	byte[] decryptedText = aesCipher.update(text, 0, read);
	    	String output = new String(decryptedText, "UTF8");	    	
	    	outputFile.println(output);
	    }
	    
	    inputFile.close();
	    outputFile.close();
		
		System.out.println("복호화된 파일은 " + originalFilename + " 입니다.");
	}
	
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("암호화할 파일명을 입력하세요 (ex. text.txt) : ");   // 사용자로부터 평문 파일명 입력 받기
		String filename = scanner.nextLine();
		
		System.out.print("설정할 비밀번호를 입력하세요 (영문 또는 숫자 16글자) : ");   // 사용자로부터 비밀키값 입력 받기 (16자) -- AES의 키는 128bit 길이제한이 있기 때문에
		String password = scanner.nextLine();
		while (password.length() != 16) {   // 입력받은 비밀번호가 16글자가 아닌 경우, 다시 입력하게 하기
			System.out.print("비밀번호는 16글자로 설정해 주세요 : ");
			password  =scanner.nextLine();
		}
		
		// -- AES 알고리즘 KEY 값 설정하는 부분 -- //
		KeyGenerator keygen = KeyGenerator.getInstance("AES");   // 사용할 알고리즘 설정
		Key aesKey = new SecretKeySpec(password.getBytes(), "AES");   // 입력받은 비밀번호를 이용하여 키값 만들기
		System.out.println("Done generating the key.");
		
		// -- File의 경로를 설정하는 부분 -- //
		String PATH = AES.class.getResource("").getPath();   // 현재 클래스의 절대 경로
		String cipher = "_cipher";   // 암호화한 파일 이름 : 파일 이름_cipher
	    String _name = "_o";   // 원본 파일 이름과 구분짓기 위해
	    String name = filename.split("\\.")[0];
	    String format = "." + filename.split("\\.")[1];
	    
	    String inputFile = PATH + filename;
	    String cipherFile = PATH + name + cipher + format;
	    String outputFile = PATH + name + _name + format;
		
		AES aes = new AES(aesKey);
		aes.Encrypt(new File(inputFile), cipherFile);   // 암호화
		
		// -- 사용자로부터 입력받은 정보를 이용하여 복호화하는 부분 -- //
		System.out.print("복호화할 파일명을 입력하세요 (ex. text.txt) : ");   // 사용자로부터 암호문 파일명 입력 받기
		String cipherFilename = scanner.nextLine();
		while (!cipherFilename.equals(name+cipher+format)) {   // 입력받은 파일명이 암호화된 파일과 이름이 다를 경우, 다시 입력하게 하기
			System.out.print("해당하는 암호화 파일이 없습니다. 다시 입력해 주세요 : ");
			cipherFilename = scanner.nextLine();
		}
		
		System.out.print("설정한 비밀번호를 입력하세요 (영문 또는 숫자 16글자) : ");   // 사용자로부터 복호에 사용할 비밀키값 입력 받기
		String pw = scanner.nextLine();
		while (pw.length() != 16 || !pw.equals(password)) {   // 입력받은 비밀번호가 16글자가 아닌 경우 or 암호할 때 설정했던 비밀번호가 아닌 경우, 다시 입력하게 하기
			System.out.print("비밀번호를 다시 입력해 주세요 : ");
			pw  =scanner.nextLine();
		}
		
		aes.Decrypt(new File(cipherFile), outputFile);   // 복호화
		
		System.out.println("Done!");
	}
}
