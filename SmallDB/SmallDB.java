package SmallDB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

import SmallDB.EditedFile.EditedFile;

/*
 * SmallDB version 2.0
 */




public class SmallDB{

	//クラス変数
	String dbfilepath;	//DB名 == DBのファイルパス
	int columnSize;		//カラム数


	//コンストラクタ (カラム無指定の場合)
	public SmallDB(String dbfilepath){
		//db名を記憶
		this.dbfilepath = dbfilepath;

		/*dbの存在確認*/
		//dbの実体になるFileの生成
		File dbfile = new File(dbfilepath);

		//もし既にDB(の実体となるファイル)があったら
		if (dbfile.exists()){
			this.columnSize = GetColumnNum();	//カラム数を取得

		//DB(の実体となるファイル)がなかったら
		}else{
			this.columnSize = 0;	//カラム数は0
			CreateDB(dbfilepath);	//DBを作成
		}
	}



	//DBの作成 (コンストラクタが動いたら内部でこいつが呼ばれるので、こいつを単独で使うことはなさそう。DBの存在確認もコンストラクタやっているので安心)
	void CreateDB(String dbfilepath){

		//EditedFileでdbfileを新規作成
		EditedFile dbfile = new EditedFile(dbfilepath);
		//DB名( = filepath)を0行目に入れておく
		dbfile.InsertLine(0,"DB:"+dbfilepath);
	}



	//カラムの追加
	//	成功したら0
	//	失敗したら-1
	public int AddColumns(String[] Columns, int[] ColumnStats, String notNullDefault){

		//新しく登録するカラムの中に重複がないかをチェック
		for(int cc=0; cc < Columns.length; cc++){
			for(int cc2=0; cc2 < cc; cc2++){
				if(cc!=cc2 && Columns[cc].matches(Columns[cc2])){
					return -1;
				}
			}
		}


		/*ここからカラムの追加*/
		EditedFile dbfile = new EditedFile(this.dbfilepath);

		//もしカラムが何もなかったら
		if(this.columnSize <= 0){

			//カラムの文字列を作成
			String ColumnLine = "Columns:";					// "Columns:a,b,c"
			for(int cc=0;cc<Columns.length;cc++){				//
				ColumnLine += Columns[cc] + ",";			//
			}								//
			ColumnLine = ColumnLine.substring(0, ColumnLine.length()-1);	// <- 最後の","を削る
			//dbへの書き込み
			dbfile.InsertLine(1,ColumnLine);

			//カラム毎のNullを許すかどうかの情報を示す文字列の作成
			String ColumnStatLine = "AllowNull:";							// "AllowNull:1,0,0"
			for(int cc=0;cc<ColumnStats.length;cc++){						//
				ColumnStatLine += String.valueOf(ColumnStats[cc]) + ",";			//
			}											//
			ColumnStatLine = ColumnStatLine.substring(0, ColumnStatLine.length()-1);		// <- 最後の","を削る
			//dbへの書き込み
			dbfile.InsertLine(2,ColumnStatLine);


		//もしカラムがすでに何かしらあったら(Recodeも何か入っているかも)
		}else{
			//元からあるColumnと新しく追加するColumnとの間で重複がないかをチェック
			String[] OldColumns = GetColumns();
			for(int cc=0; cc< Columns.length; cc++){
				for(int occ=0; occ<OldColumns.length; occ++){
					if(Columns[cc].matches(OldColumns[occ])){
						return -1;
					}
				}
			}


			//カラムラインのStringを取得
			String ColumnLine = dbfile.GetMatchLine(1);

			//取得したカラムラインの最後に、新しいカラムを追加
			for(int cc=0;cc<Columns.length;cc++){
				ColumnLine += "," + Columns[cc];
			}
			//dbのColumn行を更新
			dbfile.UpdateLine(1,ColumnLine);


			//カラムStatラインのStringを取得
			String ColumnStatLine = dbfile.GetMatchLine(2);
			//取得したカラムStatに新しいStatを追加
			for(int cc=0;cc<ColumnStats.length;cc++){
				ColumnStatLine += "," + String.valueOf(ColumnStats[cc]);
			}
			//dbのカラムStatの行を更新
			dbfile.UpdateLine(2,ColumnStatLine);


			/*ここから各レコードの更新*/
			//Recode数の取得
			int recodeNum = GetRecodeNum();
			for(int rc=0; rc<recodeNum; rc++){
				//各recodeの取得
				String eachRecode = dbfile.GetMatchLine(rc+3);
				//各新しいカラムについて
				for(int ncc=0;ncc<ColumnStats.length;ncc++){
					//もしnullが許されないなら
					if(ColumnStats[ncc] == 0){
						eachRecode += "," + notNullDefault;

					//もしnullが許されたら
					}else if(ColumnStats[ncc] == 1){
						eachRecode += ",";
					}
				}
				//更新
				dbfile.UpdateLine(rc+3,eachRecode);
			}

		}

		//カラム数の更新
		this.columnSize = GetColumnNum();

		return 0;
	}



	//カラム数を取得
	public int GetColumnNum(){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		//カラム行のStringを取得
		String ColumnLine = dbfile.GetMatchLine(1);
		//"Columns:"から始まっているはずなので、これを分離。String[]の0には""が、1には"a,b,c"のようなカラムだけが入っているはず
		String[] sepaHead = ColumnLine.split("Columns:",0);
		//もし上記の2つに分かれていなかったら何かおかしいので-1を返す
		if(sepaHead.length != 2){
			return -1;
		}
		//"a,b,c"を","でsplitして、eachColumn配列に入れる
		String[] eachColumn = sepaHead[1].split(",",0);
		//eachColumn配列のサイズ = カラム数担っているはず
		this.columnSize = eachColumn.length;

		return this.columnSize;

	}


	//カラムを取得する
	//	成功したらString[] で全カラムを
	//	失敗したらnull
	public String[] GetColumns(){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		//カラム行のStringを取得
		String ColumnLine = dbfile.GetMatchLine(1);
		//"Columns:"から始まっているはずなので、これを分離。String[]の0には""が、1には"a,b,c"のようなカラムだけが入っているはず
		String[] sepaHead = ColumnLine.split("Columns:",0);
		//もし上記の2つに分かれていなかったら何かおかしいのでnullを返す
		if(sepaHead.length != 2){
			return null;
		}
		//"a,b,c"を","でsplitして、eachColumn配列に入れる
		String[] eachColumn = sepaHead[1].split(",",0);

		return eachColumn;
	}


	//各カラムのIDを取得 (上のGetColumnsを利用)
	public int GetColumnID(String Column){
		int ColumnID = -1;
		//カラムをString[]で取得
		String[] Columns = GetColumns();
		//各カラムについて
		for(int cc=0;cc<Columns.length;cc++){
			//もし該当するカラムがあったら
			if(Columns[cc].matches(Column)){
				//カラムIDを記憶
				ColumnID = cc;
			}
		}

		return ColumnID;
	}


	//DB名を返す (コンストラクタがもう取ってきているので、返すだけで良い)
	//	コンストラクタがうまく動けば失敗はしないはず。成功したらStringを返す
	public String GetDBName(){
		return this.dbfilepath;
	}


	//DBファイルの行数を取得
	//	成功したらintでDB fileの行数(0~)を返す
	//	失敗したら -1を返す
	public int GetDBSize(){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		return dbfile.SearchAllLineNum();

	}



	//レコード数を取得
	//	成功したらint でレコード数を返す
	//	失敗してもint でなんか返ってくる。おかしいことが起こったら調査することにする。
	public int GetRecodeNum(){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		int RecodeNum = 0;

		//dbfileの各行について
		for(int lc=0; lc<GetDBSize(); lc++){
			//各行を取得
			String eachLine = dbfile.GetMatchLine(lc);
			if(eachLine.matches("DB:.*")==false && eachLine.matches("Columns:.*")==false && eachLine.matches("AllowNull:.*")==false){
				RecodeNum ++;
			}

		}

		return RecodeNum;
	}


	//カラムがnullを許すのかをチェックする
	//	true / false を返す
	public boolean checkAllowNull(int ColumnID){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		String columnStatLine = dbfile.GetMatchLine(2);
		String[] sepaHead = columnStatLine.split("AllowNull:",0);
		String[] eachStat = sepaHead[1].split(",",0);
		if(eachStat[ColumnID].matches("1")){
			return true;
		}
		return false;

	}




	//データを挿入 (一番最後に)
	//	成功したら0
	//	失敗したら-1
	public int Insert(String[] data){

		//カラム数を取得
		int ColumnNum = GetColumnNum();

		//カラム数と新しいdataの数が合っていたら
		if(ColumnNum == data.length){

			//String[] data を 1つのStringに変換
			String DataLine = "";
			for(int dc=0;dc<data.length;dc++){
				//DBのマネジメント用の文字を含んでいたら失敗させる
				if(data[dc].matches(".*,.*")==true || data[dc].matches(".*DB:.*")==true || data[dc].matches(".*Columns:.*")==true || data[dc].matches(".AllowNull:.*")==true){
					return -1;
				}
				//nullを許さないところに""を入れようとしたら失敗させる
				if(checkAllowNull(dc)==false && data[dc].matches("")){
					return -1;
				}

				DataLine += data[dc]+",";
			}
			//最後の","を削る
			DataLine = DataLine.substring(0, DataLine.length()-1);

			//最終行は何行目なのかを取得
			int finalLineNum = GetDBSize();
			//最終行にデータを書き込み
			EditedFile dbfile = new EditedFile(this.dbfilepath);
			dbfile.InsertLine(finalLineNum, DataLine);

		//カラム数とdata数が合わなかったら失敗させる
		}else{
			return -1;
		}

		return 0;
	}


	/*
	//v2で廃止
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
	*/




	//全件取得
	/*
		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+
		    .
		    .
		    .
		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+

	 */
	public String[] Select(){

		int RecodeNum = GetRecodeNum();
		String[] AllRecode = new String[RecodeNum];
		EditedFile dbfile = new EditedFile(this.dbfilepath);
		for(int rc=0; rc<RecodeNum; rc++){
			AllRecode[rc] = dbfile.GetMatchLine(rc+3);
		}

		return AllRecode;
	}



	//Recode IDを指定してレコードを取得
	//	成功したらStringでレコードを丸ごと返す
	//	失敗したらStringで""を返す
	/*
		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+

	 */
	public String Select(int RecodeID){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		String Recode = dbfile.GetMatchLine(RecodeID+3);
		return Recode;

	}



	//Recode ID と ColumnID を指定してレコードの中の 指定されたColumnのデータだけを返す
	//	成功したらStringでデータを返す
	/*
		+------+
		|      |
		+------+
	 */
	public String SelectData(int RecodeID, int ColumnID){

		//指定されたColumnのrecodeを丸々取得
		EditedFile dbfile = new EditedFile(this.dbfilepath);
		String Recode = dbfile.GetMatchLine(RecodeID+3);

		//recode丸々のStringを各カラムごとにパース
		String[] eachData = Recode.split(",",0);
		return eachData[ColumnID];

	}




	//指定カラムが指定レコードであるRecodeのIDをint[]で取得
	//	成功したら int[]
	//	失敗もしくは該当するものがなかったらしたら null
	public int[] SelectID(int columnID, String data){

		//Recode数を取得
		int RecodeNum = GetRecodeNum();

		//全件Recodeを舐める. まずは件数取得
		EditedFile dbfile = new EditedFile(this.dbfilepath);
		int matchRecodeNum = 0;
		for(int rc=0; rc<RecodeNum; rc++){
			String eachRecode = dbfile.GetMatchLine(rc+3);
			String[] eachData = eachRecode.split(",",0);
			if(eachData[columnID].matches(data)){
				matchRecodeNum ++;
			}
		}

		if(matchRecodeNum != 0){
			//返り値用のint[]
			int[] MatchRecodeID = new int[matchRecodeNum];

			//もう一回全件Recodeを舐める こんどはRecodeID取得
			int mrc = 0;
			for(int rc=0; rc<RecodeNum; rc++){
				String eachRecode = dbfile.GetMatchLine(rc+3);
				String[] eachData = eachRecode.split(",",0);
				if(eachData[columnID].matches(data)){
					MatchRecodeID[mrc] = rc;
					mrc++;
				}
			}

			return MatchRecodeID;

		}

		return null;
	}



	//指定カラムが指定レコードであるRecodeを丸々String[]で取得
	//	成功したら String[] でご所望のRecodeを
	//	失敗したらnull
	/*
		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+

		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+

		+-------+-------+     +-------+
		|       |       | ... |       |
		+-------+-------+     +-------+

	 */
	public String[] Select(int columnID, String data){

		//columnIDがdataであるRecodeのRecodeIDを取得
		int[] matchRecodeID = SelectID(columnID, data);
		if(matchRecodeID == null){
			return null;
		}

		//上で取得したRecodeIDのRecodeをString[]で取得
		String[] matchRecode = new String[matchRecodeID.length];
		for(int mrc=0;mrc<matchRecodeID.length;mrc++){
			matchRecode[mrc] = Select(matchRecodeID[mrc]);
		}

		return matchRecode;
	}


	//全件Recodeの、指定したColumnのデータだけ取得
	//	成功したらString[]で所望のData
	//	失敗したらnull
	/*
		+------+
		|      |
		+------+
		|      |
		+------+
		   .
		   .
		   .
		+------+
		|      |
		+------+

	 */
	public String[] SelectData(int columnID){
		//Recodeの全件数を取得
		int RecodeNum = GetRecodeNum();

		if(RecodeNum <= 0){
			return null;
		}


		String[] AllData = new String[RecodeNum];

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		for(int rc=0; rc<RecodeNum; rc++){
			String eachRecode  = dbfile.GetMatchLine(rc+3);
			AllData[rc] = eachRecode.split(",",0)[columnID];
		}

		return AllData;
	}


	//指定したカラムが指定したレコードであるRecodeの、指定したColumnのデータだけ取得
	//	成功したらString[]
	//	失敗もしくは該当するデータがなかったらnull
	/*
		+------+
		|      |
		+------+

		+------+
		|      |
		+------+

		+------+
		|      |
		+------+
	 */
	public String[] SelectData(int checkColumnID, String data, int getColumnID){

		int[] matchRecodeID = SelectID(checkColumnID, data);

		if(matchRecodeID != null){
			String[] selectedData = new String[matchRecodeID.length];

			for(int dc=0; dc<matchRecodeID.length; dc++){
				selectedData[dc] = SelectData(matchRecodeID[dc], getColumnID);
			}		

			return selectedData;

		}

		return null;
	}


	//指定したレコードを削除
	public int Delete(int RecodeID){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		int result = dbfile.DeleteMatchLine(RecodeID+3);
		return result;
	}


	//指定したレコードの指定したカラムを指定したデータに更新
	public void Update(int RecodeID, String Column, String Data){

		EditedFile dbfile = new EditedFile(this.dbfilepath);
		//古いRecodeの取得
		String OldData = dbfile.GetMatchLine(RecodeID+3);

		//指定されたカラムのカラムIDを取得
		int ColumnID = GetColumnID(Column);

		//古いRecodeを各カラム毎にパース
		String[] eachData = OldData.split(",",0);

		//指定されたカラムのデータを更新
		eachData[ColumnID] = Data;

		//改めてDBにInsert
		Insert(eachData);

	}

}
