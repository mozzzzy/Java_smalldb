package SmallDB.FileEdit;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/*
ファイル編集用クラス
*/
public class FileEdit{

	//ファイル名を入れるクラス変数
	String Filepath = "";

	//コンストラクタ (ファイル名をクラス変数に入れる)
	public FileEdit(String Filepath){
		this.Filepath = Filepath;
	}


	//ファイルの行数を取得
	int GetLineNum(){
		int LineNum = 0;
		File editfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			String oneline;
			while((oneline = br.readLine()) != null){
				LineNum++;	
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.SearchLineNum] Error. IOException.");
		}

		return LineNum;
	}



	//keywordを含む行の行数(何行あるか)を取得
	int SearchLineNum(String keyword){
		int MatchLineNum = 0;
		File editfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*"+keyword+".*")){
					MatchLineNum ++;	
				}
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.SearchLineNum] Error. IOException.");
		}

		return MatchLineNum;
	}



	//keywordを含む行の行番号(上は行の数)を取得
	public int[] SearchLine(String keyword){
		int MatchLineNum = SearchLineNum(keyword);
		int [] MatchLines = new int[MatchLineNum];

		File editfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			int Line = 0;
			int ac = 0;
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*"+keyword+".*")){
					MatchLines[ac] = Line;
					ac++;	
				}
				Line++;
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.SearchLineNum] Error. IOException.");
		}

		return MatchLines;

		
	}


	//指定した行番号の行を削除
	public void DelLine(int Line){
		int LineNum = GetLineNum();
		String[] AllLine = new String[LineNum];

		File editfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			for(int lc=0;lc<LineNum;lc++){
				AllLine[lc] = br.readLine();
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.DelLine] Error. IOException.");
		}


		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(editfile));

			for(int lc=0;lc<LineNum;lc++){
				if(lc!=Line){
					bw.write(AllLine[lc]+"\n");
				}
			}
			bw.close();
		}catch(IOException e){
			System.out.println("[FileEdit.DelLine] Error. IOException.");
		}
	}


	//指定した行番号の行をString型で取得
	public String GetLine(int Line){

		String ReturnString;
		//全行数を取得
		int LineNum = GetLineNum();
		//全行を記録するString型の配列を生成
		String[] AllLine = new String[LineNum];

		//編集するファイルのオブジェクトを生成
		File editfile = new File(this.Filepath);

		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			for(int lc=0;lc<LineNum;lc++){
				AllLine[lc] = br.readLine();
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.DelLine] Error. IOException.");
		}


		return AllLine[Line];
	}









	//行挿入
	public void AddLine(int Line, String LineString){
		int LineNum = GetLineNum();
		String[] AllLine = new String[LineNum];

		File editfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editfile));

			//全行読み込み
			for(int lc=0;lc<LineNum;lc++){
				AllLine[lc] = br.readLine();
			}
			br.close();

		}catch(IOException e){
			System.out.println("[FileEdit.DelLine] Error. IOException.");
		}


		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(editfile));

			for(int lc=0;lc<LineNum+1;lc++){
				if(lc<Line){
					bw.write(AllLine[lc]+"\n");
				}else if(lc==Line){
					bw.write(LineString+"\n");
				}else if(lc > Line){
					bw.write(AllLine[lc-1]+"\n");
				}
			}
			bw.close();
		}catch(IOException e){
			System.out.println("[FileEdit.DelLine] Error. IOException.");
		}
	}




	//行置換
	public void UpdateLine(int Line,String LineString){
		DelLine(Line);
		AddLine(Line,LineString);
	}
}
