package awsLinkMicro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AwsLinkMicro {
	public static void main(String[] args) throws IOException {
		 
		/*
		 * 사용법 가이드 
		 * 1. aws 관련링크에서 segments.gz 파일을 다운로드 받습니다 
		 * 2. 해당 파일 압축을 푼후, 안에 내용을 txt 파일로 다시 저장합니다.
		 */		 
		AwsLinkMicro awkLinkMicro = new AwsLinkMicro();
		 
		 //String textFileLink = "C://JavaProject/awsLinkPath/crawl-dataCC-MAIN-2020-16.txt";
		 String textFileLink = "C://JavaProject/awsLinkPath/crawl-dataCC-MAIN-2020-24.txt";
		 		// 3.textFileLink 에 해당 txt 파일의 위치를 지정해 줍니다.
		 //String baseLink = "sudo -H -u filadmin aws s3 sync s3://commoncrawl/crawl-data/CC-MAIN-2020-16 /mnt/nas/crawl-data/CC-MAIN-2020-16";
		 String baseLink = "sudo -H -u filadmin aws s3 sync s3://commoncrawl/crawl-data/CC-MAIN-2020-24 /mnt/dataset/crawl-data/CC-MAIN-2020-24";
		 		// 4.aws 기본 링크를 적어 줍니다.
		 baseLink += " --exclude \"*\" "; 
		 int linkCount = 10;
		 		// 5. 링크나누는 갯수를 linkCount 에 지정합니다 (예 : 3 -> 3조각)
		 List<String> resultLink = awkLinkMicro.awsLinkMacro(textFileLink,baseLink,linkCount);
		 		// 6. 해당 스크립트를 실행합니다. 그러면 콘솔에 링크가 표시됩니다. (해당 링크들은는 resultLink 에 또한 저장됩니다.)
		 		// 7. 빈 텍스트파일을 하나 열어서, 콘솔 전체를 복사 붙여 넣기 합니다.
		 		// 8. 각각의 링크를 따로  각각의 tmux new -t 에 설정 합니다.
    }
	//머야이거?
	
	public List<String> awsLinkMacro (String textFileLink,String baseLink, int linkCount){
		
		 File file = new File(textFileLink);
	        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	            String line;
	            List<String> links = new ArrayList<String>();
	            int counter=0;
	            while ((line = br.readLine()) != null) {
	                line = line.substring(line.indexOf("segments"));
	            	links.add(line);
	            	counter++;
	            }   
	            	System.out.println("파일에서 읽어온 폴더 수 : " + counter);
	            	counter = 0;
	            // listMixer : 다운로드 링크시에, 앞짜리 디랙토리들을 먼저 체크하기때문에, 각각에 링크에 안에 디랙토리 순서를 조작하여, 다운로드 시작이 빠를수 있게 하였습니다.
	            links = listMixer(links,linkCount);
	            	System.out.println("폴더링크 믹스한다음 결과 수 :" + links.size());
	            int eachLinkSize = links.size()/linkCount;
	            // eachLinkSize : 각각에 링크에 최소한 몇개의 디랙토리가 들어가는지 숫자
	            int sizeBuffer = links.size()%linkCount;
	            // sizeBuffer : 최소한의 디랙토리를 제외하고, 다머니 몇개의 디랙토리들이  남는지.
	            int startIndex = 0;
	            int endIndex = 0;
	            
	            List<String> newLinks = new ArrayList<String>();
	            //링크 별로 반복
	            for(int i=0;i<linkCount;i++) {
	            	String tempLink = "";
	            	if(sizeBuffer>0) {
	            		//sizeBuffer 가 존재할경우, 최소 디랙토리에서 하나씩 더 추가한후, buffer 을 하나 줄입니다.
	            		sizeBuffer--;
	            		endIndex += eachLinkSize + 1;
	            	}else {
	            		endIndex += eachLinkSize;
	            		//sizeBuffer 가 존재 안할경우, 그냥 최소 디랙토리 숫자로
	            	}
		            for(int j=startIndex; j<endIndex;j++) {
		            	tempLink += " --include \"" + links.get(j) + "*\"";
		            	//각각의 디랙토리를 --include 로 추가 합니다.
	            		counter++;
		            }     
		            startIndex = endIndex;
		            System.out.println("추가된 폴더 링크 수 :" + counter);
		            counter =0;
	            	System.out.println(baseLink + tempLink);
	            	newLinks.add(baseLink + tempLink);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		
		return null;
	}
	
	
	
	public List<String> listMixer (List<String> list,int linkCount){
		List<String> newList = new ArrayList<String>();
		for(int j = 0;j<linkCount;j++) {	
			for(int i=j;i<list.size();i += linkCount ) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
	
	
}
