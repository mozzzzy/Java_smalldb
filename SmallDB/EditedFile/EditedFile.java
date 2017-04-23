package SmallDB.EditedFile;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/*
編集するファイルのクラス
*/
public class EditedFile{

	//ファイル名を入れる変数
	String Filepath = "";

	//コンストラクタ (ファイル名をクラス変数に入れる)
	public EditedFile(String Filepath){
		//ファイル名を受けとって、変数に代入
		this.Filepath = Filepath;
	
		//もしファイルがなかったら、新規作成
		File ef = new File(this.Filepath);
		if(ef.exists() == false){
			try{
				ef.createNewFile();
			}catch(IOException e){
				System.out.println("[EditedFile.EditedFile] Error. IOException.");
			}
		}		

	}


	//ファイルの全行数(ファイルが何行あるか)を取得
	//	0行 ~ を返す。
	//	失敗したら-1を返す
	public int SearchAllLineNum(){
		int LineNum = 0;
		File editedfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editedfile));

			//全行読み込み
			String oneline;
			while((oneline = br.readLine()) != null){
				//1行読み込むごとに、行数を++
				LineNum++;	
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.SearchAllLineNum] Error. IOException.");
			return -1;
		}

		return LineNum;
	}



	//keywordを含む行の行数(何行あるか)を取得
	//	Matchする行がなかったら0を返す
	//	例外が起こったら-1を返す
	public int SearchMatchLineNum(String keyword){
		int MatchLineNum = 0;
		File editedfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editedfile));

			//全行読み込み
			String oneline;
			while((oneline = br.readLine()) != null){
				//もしキーワードを含んでいたら
				if(oneline.matches(".*"+keyword+".*")){
					MatchLineNum ++;	
				}
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.SearchMatchLineNum] Error. IOException.");
			return -1;
		}

		return MatchLineNum;
	}



	//keywordを含む行の行番号(上は行の数。このメソッドとは違う。)を取得
	//	返り値は、intの配列。中には、行番号(0行目から数える)が入っている
	//	Matchする行がなかった, もしくは例外が起こったら nullを返す
	public int[] SearchMatchLine(String keyword){

		//キーワードを含む行が何行あるかを調べる
		int MatchLineNum = SearchMatchLineNum(keyword);

		if(MatchLineNum == 0){
			return null;
		}

		//キーワードを含む行番号を入れるための配列
		int [] MatchLines = new int[MatchLineNum];

		File editedfile = new File(this.Filepath);
		try{
			BufferedReader br = new BufferedReader(new FileReader(editedfile));

			//全行読み込み
			int Line = 0;	//whileでその時々に見ている行番号のカウンター
			int ac = 0;	//マッチした行数のカウンター
			String oneline;
			while((oneline = br.readLine()) != null){
				//もしkeywordにマッチしたら
				if(oneline.matches(".*"+keyword+".*")){
					//マッチした行番号を記憶
					MatchLines[ac] = Line;
					ac++;	
				}
				Line++;
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.SearchMatchLine] Error. IOException.");
			return null;
		}

		return MatchLines;
	}


	//指定した行番号の行を削除
	//	成功したら0を返す
	//	失敗したら-1を返す
	public int DeleteMatchLine(int Line){
		//行数取得
		int AllLineNum = SearchAllLineNum();
		if(Line > AllLineNum-1){
			System.out.println("[EditedFile.DeleteMatchLine] Error. Deleted Line Number is too large.");
			return -1;
		}

		//全行のデータを入れるための配列(サイズは全行数)
		String[] AllLine = new String[AllLineNum];
		//Fileのインスタンス作成
		File editedfile = new File(this.Filepath);

		try{

			//全行読み込み
			BufferedReader br = new BufferedReader(new FileReader(editedfile));
			for(int lc=0;lc<AllLineNum;lc++){
				AllLine[lc] = br.readLine();
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.DeleteMatchLine] Error. IOException.");
			return -1;
		}


		try{
			//削除される行以外を改めて上書き
			BufferedWriter bw = new BufferedWriter(new FileWriter(editedfile));
			for(int lc=0;lc<AllLineNum;lc++){
				if(lc!=Line){
					bw.write(AllLine[lc]+"\n");
				}
			}
			bw.close();
		}catch(IOException e){
			System.out.println("[EditedFile.DeleteMatchLine] Error. IOException.");
			return -1;
		}

		return 0;
	}



	//指定した行番号(0から数える)の行をString型で取得
	//	成功したら所望の行のデータをStringで返す
	//	失敗したら""を返す
	public String GetMatchLine(int Line){

		//返り値用の変数
		String ReturnString = "";

		//編集するファイルのオブジェクトを生成
		File editedfile = new File(this.Filepath);
		try{

			String oneline = "";
			int LineNum = 0;
			BufferedReader br = new BufferedReader(new FileReader(editedfile));
			while((oneline = br.readLine()) != null){
				if(LineNum == Line){
					ReturnString = oneline;
					break;
				}
				
				//1行読み込むごとに、行数を++
				LineNum++;
				
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.GetMatchLine] Error. IOException.");
		}

		return ReturnString;

	}




	//行挿入
	//	成功したら0, 失敗したら-1を返す
	public int InsertLine(int LineNum, String LineString){
		//全行数を取得
		int AllLineNum = SearchAllLineNum();
		if(LineNum > AllLineNum){
			System.out.println("[EditedFile.InsertLine] Error. Insert Line Number is too large.");
			return -1;	
		}


		//全行を入れるためのString配列
		String[] AllLine = new String[AllLineNum];

		File editedfile = new File(this.Filepath);
		try{
			//全行読み込み
			BufferedReader br = new BufferedReader(new FileReader(editedfile));
			for(int lc=0;lc<AllLineNum;lc++){
				AllLine[lc] = br.readLine();
			}
			br.close();

		}catch(IOException e){
			System.out.println("[EditedFile.InsertLine] Error. IOException.");
			return -1;
		}


		try{
			//全行+追加行の書き込み
			BufferedWriter bw = new BufferedWriter(new FileWriter(editedfile));
			for(int lc=0;lc<AllLineNum+1;lc++){
				if(lc<LineNum){
					bw.write(AllLine[lc]+"\n");
				//もし
				}else if(lc==LineNum){
					bw.write(LineString+"\n");
				}else if(lc > LineNum){
					bw.write(AllLine[lc-1]+"\n");
				}
			}
			bw.close();
		}catch(IOException e){
			System.out.println("[EditedFile.InsertLine] Error. IOException.");
			return -1;
		}

		return 0;
	}




	//行置換
	//	成功したら0 , 失敗したら-1を返す
	public int UpdateLine(int LineNum, String LineString){
		int resultDelete = 0;
		int resultInsert = 0;
		//行削除
		resultDelete = DeleteMatchLine(LineNum);
		//行削除が成功したら
		if(resultDelete == 0){
			//行挿入
			resultInsert = InsertLine(LineNum,LineString);
		}else{
			return -1;
		}
		return resultInsert;
	}
}
