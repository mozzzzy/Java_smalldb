/*
 * SmallDB version 1.0
 */


package SmallDB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

//import FileEdit.FileEdit;
import SmallDB.FileEdit.FileEdit;

/*
内製DB クラス
*/
public class SmallDB{

	//クラス変数
	String dbname = "";	//DB名
	int columnSize = 0;	//カラム数



	//コンストラクタ (カラム無指定の場合)
	public SmallDB(String dbname){
		//db名を記憶
		this.dbname = dbname;

		//dbの実体になるFileの生成
		File dbfile = new File(dbname);

		//もし既にDB(の実体となるファイル)があったら
		if (dbfile.exists()){
			System.out.println("[SmallDB.SmallDB] DB \""+dbname+"\" is already exist.");	//メッセージを出力
			this.columnSize = GetColumnNum();	//カラム数を取得

		//DB(の実体となるファイル)がなかったら
		}else{
			this.columnSize = 0;	//カラム数は0
			CreateDB(dbname);	//DBを作成
		}
	}



	//DBの作成
	public void CreateDB(String dbname){
		System.out.println("[SmallDB.CreateDB] Create new Database \""+dbname+"\".");
		File dbfile = new File(dbname);
		try{
			FileWriter filewriter = new FileWriter(dbfile);
			filewriter.write("DB:"+dbname+"\n");	//DB:Database名 を書き込み
			filewriter.close();
		}catch(IOException e){
			System.out.println("[SmallDB.CreateDB] Error. Can't create new Database \""+dbname+"\".");
		}
	}



	//カラムの追加
	public void AddColumns(String[] Columns){

		//ご挨拶する
		System.out.print("[SmallDB.AddColumns] Add column ");		//Add column "a" "b" "c" to "dbname".\n
		for(int cc=0;cc<Columns.length;cc++){				//
			System.out.print("\""+Columns[cc]+"\" ");		//
		}								//
		System.out.print("to \""+this.dbname+"\".");			//
		System.out.print("\n");						//




		File dbfile = new File(this.dbname);
		//もしカラムが何もなかったら
		if(this.columnSize==0){
			try{
				FileWriter filewriter = new FileWriter(dbfile,true);

				//カラム行の文字列を生成
				String ColumnLine = "Columns:";					//Columns:a,b,c\n  の文字列を作成
				for(int cc=0;cc<Columns.length;cc++){				//
					ColumnLine += Columns[cc] + ",";			//
				}								//
				ColumnLine = ColumnLine.substring(0, ColumnLine.length()-1);	//
				ColumnLine += "\n";						//


				//カラム数をクラス変数に再登録
				this.columnSize += Columns.length;				//カラム数を更新

				//DB(の実体であるファイル)に書き込み
				filewriter.write(ColumnLine);					//書き込み
				filewriter.close();

			}catch(IOException e){
				System.out.println("[SmallDB.AddCoumuns] Can't add columns to Database \""+dbname+"\".");
			}


		//もし何か既にカラムがあったら
		}else{
			//dbfileを編集するためのオブジェクト
			FileEdit editfile = new FileEdit(this.dbname);

			//"Columns:"の行番号を取得
			int ColumnLineNum[] = editfile.SearchLine("Columns:");
			if(ColumnLineNum.length==1){
				//columnを取得
				String[] columns = GetColumns();

				//"Columns:"の行をStringで取得
				String ColumnLine = editfile.GetLine(ColumnLineNum[0]);
				for(int cc=0;cc<Columns.length;cc++){				//

					//ユニークかどうかのチェック
					int flag_unique = 0;
					for(int cc2=0;cc2<columns.length;cc2++){
						if(columns[cc2].matches(Columns[cc])){
							flag_unique = 1;
						}
					}
					if(flag_unique==0){
						ColumnLine = ColumnLine + "," + Columns[cc];
					}else{
						System.out.println("[SmallDB.AddColumns]Additional Column \""+Columns[cc]+"\" not unique.");
					}

				}
				

				editfile.UpdateLine(ColumnLineNum[0],ColumnLine);
			}

		}

		this.columnSize = GetColumnNum();
	}



	//カラム数を取得
	public int GetColumnNum(){

		try{
			//カラム行を探す
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
			//一行ずつ読み込む
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.startsWith("Columns:")){
					String values = oneline.split(":")[1];
					String[] eachColumn = oneline.split(",");
					this.columnSize = eachColumn.length;
				}
			}
			br.close();
		}catch(IOException e){
			System.out.println("[SmallDB.GetColumnNum] Error. IOException.");
		}

		return this.columnSize;
	}


	//カラムを取得する
	public String[] GetColumns(){

		String[] Columns = new String[GetColumnNum()];

		try{
			//カラム行を探す
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
			//一行ずつ読み込む
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.startsWith("Columns:")){
					String values = oneline.split(":")[1];
					Columns = values.split(",");
				}
			}
			br.close();
		}catch(IOException e){
			System.out.println("[SmallDB.GetColumns] Error. IOException.");
		}

		return Columns;
	}


	//各カラムのIDを取得
	public int GetColumnID(String Column){
	
		int ColumnID = -1;
		String[] Columns = GetColumns();
		for(int cc=0;cc<Columns.length;cc++){
			if(Columns[cc].matches(Column)){
				ColumnID = cc;
			}
		}

		if(ColumnID == -1){
			System.out.println("[SmallDB.GetColumnID] Column \""+Column+"\" not exist.");
		}

		return ColumnID;	
	}


	//DB名を取得
	public String GetDBName(){
		return dbname;
	}




	//DBファイルの行数を取得
	public int GetDBSize(){
		
		int lc = 0;	//line counter
		try{
			//読み込み用BufferReader
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
			//一行ずつ読み込む
			String oneline;
			while((oneline = br.readLine()) != null){
				lc++;
			}
			br.close();
		}catch(IOException e){
			System.out.println("[SmallDB.GetDBSize] GetDBSize: Error. IOException.");
		}

		return lc;

	}


	//レコード数を取得
	public int GetRecodeNum(){
		
		int lc = 0;	//line counter
		try{
			//読み込み用BufferReader
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
			//一行ずつ読み込む
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){
					lc++;
				}
			}
			br.close();
		}catch(IOException e){
			System.out.println("[SmallDB.GetDBSize] GetDBSize: Error. IOException.");
		}

		return lc;

	}





	//データを挿入
	public void Insert(String[] data){

		int ColumnNum = GetColumnNum();
		//System.out.println("##ColumnNum : "+ColumnNum);
		if(ColumnNum == data.length){


			String DataLine = "";
			for(int dc=0;dc<data.length;dc++){
				DataLine += data[dc]+",";
			}

			DataLine = DataLine.substring(0, DataLine.length()-1);
			DataLine += "\n";

			File dbfile = new File(dbname);
			try{
				FileWriter filewriter = new FileWriter(dbfile,true);
				filewriter.write(DataLine);
				filewriter.close();
			}catch(IOException e){
				System.out.println("[SmallDB.Insert] Error. Can't Insert new Data.");
			}

		}else{
			System.out.println("[SmallDB.Insert] Error. Different (data size <=> column num)");
		}
	}


	//データを特定のIDに挿入
	public void Insert(String[] data, int ID){

		//カラム数を取得
		int ColumnNum = GetColumnNum();

		//もしDBのカラム数と挿入するデータのカラム数が同じだったら
		if(ColumnNum == data.length){

			//"data0,data1,data2,...,dataN"の形式の文字列を作成
			String DataLine = "";
			for(int dc=0;dc<data.length;dc++){
				DataLine += data[dc]+",";
			}
			DataLine = DataLine.substring(0, DataLine.length()-1);	//最後の "," は "\n" に変える

			//ID X番がファイル上で何行目なのかを計算
			int DBFileLineNum = GetDBSize();
			int RecodeLineNum = GetRecodeNum();
			int InsertLine = DBFileLineNum - RecodeLineNum + ID;
			FileEdit ef = new FileEdit(this.dbname);
			ef.AddLine(InsertLine,DataLine);
		}else{
			System.out.println("[SmallDB.Insert] Error. Different (data size <=> column num)");
		}
	}





	//全件取得
	public String[] Select(){

		int DataSize = 0;
		try{
			//DB(の実体であるファイル)を全行読み込むためのBuffredReader
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));

			//データ数を計算
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){
					DataSize ++;
				}
			}
			//BufferedReaderをクローズ
			br.close();
	
		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");
		}

		String[] ReturnData = new String[DataSize];
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
	
			//データを取得
			int dc = 0;
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){
					ReturnData[dc] = oneline;
					dc++;
				}
			}
		
			br.close();


		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");

		}
		return ReturnData;
	}



	//指定カラムが指定レコードであるデータを取得
	public String[] Select(String column, String data){

		int check_column = -1;
		String[] Columns = GetColumns();
		for(int cc=0;cc<Columns.length;cc++){
			if(Columns[cc].matches(column)){
				check_column = cc;
			}
		}

		if(check_column == -1){
			System.out.println("[SmallDB.Select] Column \""+column+"\" not exist.");
			return null;
		}

		int DataSize = 0;
		try{
			//DB(の実体であるファイル)を全行読み込むためのBuffredReader
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));

			//データ数を計算
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){

					String[] eachdata = oneline.split(",");
					if(eachdata[check_column].matches(data)){
						DataSize ++;
					}
					
				}
			}
			//BufferedReaderをクローズ
			br.close();
	
		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");
		}

		String[] ReturnData = new String[DataSize];
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
	
			//データ数を計算
			int dc = 0;
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){

					String[] eachdata = oneline.split(",");
					if(eachdata[check_column].matches(data)){
						ReturnData[dc] = oneline;
						dc++;
					}
					
				}
			}
		
			br.close();


		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");

		}
		return ReturnData;
	}


	//指定カラムが指定レコードであるデータのIDを取得
	public int[] SelectID(String column, String data){

		//ご所望のカラムIDを取得
		int check_column = -1;
		String[] Columns = GetColumns();
		for(int cc=0;cc<Columns.length;cc++){
			if(Columns[cc].matches(column)){
				check_column = cc;
			}
		}

		//もし所望のカラムがなかったら
		if(check_column == -1){
			System.out.println("[SmallDB.Select] Column \""+column+"\" not exist.");
			return null;
		}

		int DataSize = 0;
		try{
			//DB(の実体であるファイル)を全行読み込むためのBuffredReader
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));

			//データ数を計算
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){

					String[] eachdata = oneline.split(",");
					if(eachdata[check_column].matches(data)){
						DataSize ++;
					}
					
				}
			}
			//BufferedReaderをクローズ
			br.close();
	
		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");
		}

		int[] ReturnData = new int[DataSize];
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
	
			//データを検索
			int dc = 0;
			int adc = 0;	//IDを計算するためのカウンタ
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){
					adc++;
					String[] eachdata = oneline.split(",");
					if(eachdata[check_column].matches(data)){
						ReturnData[dc] = adc;
						dc++;
					}
					
				}
			}
		
			br.close();


		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");

		}
		return ReturnData;
	}



	//指定したIDのレコードを取得
	public String Select(int RecodeID){

		String ReturnData = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.dbname));
	
			//データを取得
			int dc = 0;
			String oneline;
			while((oneline = br.readLine()) != null){
				if(oneline.matches(".*:.*")==false){
					if(dc==RecodeID){
						ReturnData = oneline;
					}
					dc++;
				}
			}
		
			br.close();


		}catch(IOException e){
			System.out.println("[SmallDB.Select] Error. IOException.");

		}
		return ReturnData;
	}






	//指定したレコードの指定したカラムを指定したデータに更新
	public void Update(int RecodeID, String Column, String Data){
		//古いデータを取得
		String OldData = Select(RecodeID);

		//カラム番号を取得
		int check_column = GetColumnID(Column);


		//各カラムでデータをパース
		String EachOldData[] = OldData.split(",");
		EachOldData[check_column] = Data;

		//レコードの文字列を再構成
		Delete(RecodeID);
		Insert(EachOldData,RecodeID);
	}



	//指定したレコードを削除
	public void Delete(int RecodeID){
	
		//ID X番がファイル上で何行目なのかを計算
		int DBFileLineNum = GetDBSize();
		int RecodeLineNum = GetRecodeNum();
		int DelLine = DBFileLineNum - RecodeLineNum + RecodeID;

		FileEdit ef = new FileEdit(this.dbname);
		ef.DelLine(DelLine);
	
	
	}

}
