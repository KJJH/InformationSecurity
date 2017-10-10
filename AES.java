import java.io.*;
import java.util.*;
import java.security.Key;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	Key AESKEY;
	
	// Ű���� �������ִ� ������
	public AES(Key key) {
		this.AESKEY = key;
	}
	
	/*  -> ���� �о�� ��ȣȭ��  �ؼ� �ٽ� ���Ϸ� �����ϴ� �Լ�
	 *   originalFile : ���� ���� (��ȣȭ�� ��� ����)
	 *   cipherFilename : ��ȣȭ�� ������ �̸� (�� �̸����� ��ȣȭ ������ �����)
	 */
	public void Encrypt(File originalFile, String cipherFilename) throws Exception {
		System.out.println("������ ��ȣȭ�մϴ�.");
		
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");   // getInstance(�˰��� �̸�, ���, �е�)
	    aesCipher.init(Cipher.ENCRYPT_MODE, AESKEY);
	    
	    BufferedReader inputFile = new BufferedReader(new FileReader(originalFile));   // �о���� ��� ����
	    FileOutputStream outputFile = new FileOutputStream(cipherFilename);   // ��ȣȭ�Ǿ� ������� ����
	    
	    // -- ������ ���� ������ �о� ��ȣȭ�Ͽ� �����ϴ� �κ� -- //
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
		
		System.out.println("��ȣȭ�� ������ " + cipherFilename + " �Դϴ�.");
	}
	
	/*  -> ��ȣȭ�� ���� �о�� ��ȣȭ��  �ؼ� �ٽ� ���� ���Ϸ� �����ϴ� �Լ�
	 *   cipherFile : ��ȣȭ�� ���� (��ȣȭ�� ��� ����)
	 *   originalFilename : ��ȣȭ�� ������ �̸� (�� �̸����� ��ȣȭ ������ �����)
	 */
	public void Decrypt(File cipherFile, String originalFilename) throws Exception {
		System.out.println("������ ��ȣȭ�մϴ�.");
		
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");   // getInstance(�˰��� �̸�, ���, �е�)
	    aesCipher.init(Cipher.DECRYPT_MODE, AESKEY);
	    
	    FileInputStream inputFile = new FileInputStream(cipherFile);   // �о���� ��ȣȭ ����
	    PrintWriter outputFile = new PrintWriter(originalFilename);   // ��ȣȭ�Ͽ� ������� ����
	    
	    // -- ������ byte�� �о�� ��ȣȭ�Ͽ� ���ڿ��� ��ȯ�Ͽ� �����ϴ� �κ� -- //
	    int read;
	    byte[] text = new byte[1024]; 
	    while((read = inputFile.read(text)) != -1) {
	    	byte[] decryptedText = aesCipher.update(text, 0, read);
	    	String output = new String(decryptedText, "UTF8");	    	
	    	outputFile.println(output);
	    }
	    
	    inputFile.close();
	    outputFile.close();
		
		System.out.println("��ȣȭ�� ������ " + originalFilename + " �Դϴ�.");
	}
	
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("��ȣȭ�� ���ϸ��� �Է��ϼ��� (ex. text.txt) : ");   // ����ڷκ��� �� ���ϸ� �Է� �ޱ�
		String filename = scanner.nextLine();
		
		System.out.print("������ ��й�ȣ�� �Է��ϼ��� (���� �Ǵ� ���� 16����) : ");   // ����ڷκ��� ���Ű�� �Է� �ޱ� (16��) -- AES�� Ű�� 128bit ���������� �ֱ� ������
		String password = scanner.nextLine();
		while (password.length() != 16) {   // �Է¹��� ��й�ȣ�� 16���ڰ� �ƴ� ���, �ٽ� �Է��ϰ� �ϱ�
			System.out.print("��й�ȣ�� 16���ڷ� ������ �ּ��� : ");
			password  =scanner.nextLine();
		}
		
		// -- AES �˰��� KEY �� �����ϴ� �κ� -- //
		KeyGenerator keygen = KeyGenerator.getInstance("AES");   // ����� �˰��� ����
		Key aesKey = new SecretKeySpec(password.getBytes(), "AES");   // �Է¹��� ��й�ȣ�� �̿��Ͽ� Ű�� �����
		System.out.println("Done generating the key.");
		
		// -- File�� ��θ� �����ϴ� �κ� -- //
		String PATH = AES.class.getResource("").getPath();   // ���� Ŭ������ ���� ���
		String cipher = "_cipher";   // ��ȣȭ�� ���� �̸� : ���� �̸�_cipher
	    String _name = "_o";   // ���� ���� �̸��� �������� ����
	    String name = filename.split("\\.")[0];
	    String format = "." + filename.split("\\.")[1];
	    
	    String inputFile = PATH + filename;
	    String cipherFile = PATH + name + cipher + format;
	    String outputFile = PATH + name + _name + format;
		
		AES aes = new AES(aesKey);
		aes.Encrypt(new File(inputFile), cipherFile);   // ��ȣȭ
		
		// -- ����ڷκ��� �Է¹��� ������ �̿��Ͽ� ��ȣȭ�ϴ� �κ� -- //
		System.out.print("��ȣȭ�� ���ϸ��� �Է��ϼ��� (ex. text.txt) : ");   // ����ڷκ��� ��ȣ�� ���ϸ� �Է� �ޱ�
		String cipherFilename = scanner.nextLine();
		while (!cipherFilename.equals(name+cipher+format)) {   // �Է¹��� ���ϸ��� ��ȣȭ�� ���ϰ� �̸��� �ٸ� ���, �ٽ� �Է��ϰ� �ϱ�
			System.out.print("�ش��ϴ� ��ȣȭ ������ �����ϴ�. �ٽ� �Է��� �ּ��� : ");
			cipherFilename = scanner.nextLine();
		}
		
		System.out.print("������ ��й�ȣ�� �Է��ϼ��� (���� �Ǵ� ���� 16����) : ");   // ����ڷκ��� ��ȣ�� ����� ���Ű�� �Է� �ޱ�
		String pw = scanner.nextLine();
		while (pw.length() != 16 || !pw.equals(password)) {   // �Է¹��� ��й�ȣ�� 16���ڰ� �ƴ� ��� or ��ȣ�� �� �����ߴ� ��й�ȣ�� �ƴ� ���, �ٽ� �Է��ϰ� �ϱ�
			System.out.print("��й�ȣ�� �ٽ� �Է��� �ּ��� : ");
			pw  =scanner.nextLine();
		}
		
		aes.Decrypt(new File(cipherFile), outputFile);   // ��ȣȭ
		
		System.out.println("Done!");
	}
}
