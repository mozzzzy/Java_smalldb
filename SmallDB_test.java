import SmallDB.SmallDB;

public class SmallDB_test{

	public static void main(String[] args){
		//DBオブジェクトの生成	(存在しなければDB fileを新規作成)
		SmallDB testDB = new SmallDB("./testDB.smdb");
		int result = 0;

		/* =============カラム関係のテスト============== */
		//カラムの初めての登録
		String[] Columns = {"column0","column1","column2","column3"};
		int[] ColumnStats = {0,1,0,1};
		result = testDB.AddColumns(Columns,ColumnStats,"NULL_SPACE");
		System.out.println("result of AddColumns : "+result);

		//カラム数の取得
		System.out.println("now Column Num : "+testDB.GetColumnNum());

		//カラムの取得
		String[] GotColumns = testDB.GetColumns();
		System.out.println("columns is follow...");
		for(int cc=0; cc< GotColumns.length; cc++){
			System.out.println("\t"+"[columnID "+cc+"] "+GotColumns[cc]);
		}
		System.out.println("");


		//ダブりのあるカラムを入れて失敗させる
		String[] Columns2 = {"column0","column4","column5","column6"};
		int[] ColumnStats2 = {0,1,0,1};
		result = testDB.AddColumns(Columns2,ColumnStats2,"NULL_SPACE");
		System.out.println("result of AddColumns : "+result);

		//カラム数の取得
		System.out.println("now Column Num : "+testDB.GetColumnNum());

		//カラムの取得
		GotColumns = testDB.GetColumns();
		System.out.println("columns is follow...");
		for(int cc=0; cc< GotColumns.length; cc++){
			System.out.println("\t"+"[columnID "+cc+"] "+GotColumns[cc]);
		}
		System.out.println("");


		//ダブりのないカラムを追加
		String[] Columns3 = {"column4","column5","column6"};
		int[] ColumnStats3 = {0,1,0};
		result = testDB.AddColumns(Columns3,ColumnStats3,"NULL_SPACE");
		System.out.println("result of AddColumns : "+result);

		//カラム数の取得
		System.out.println("now Column Num : "+testDB.GetColumnNum());

		//カラムの取得
		GotColumns = testDB.GetColumns();
		System.out.println("columns is follow...");
		for(int cc=0; cc< GotColumns.length; cc++){
			System.out.println("\t"+"[columnID "+cc+"] "+GotColumns[cc]+" (result of GetColumnID :"+testDB.GetColumnID(GotColumns[cc])+" )");
		}
		System.out.println("");

		//nullを許すかどうかの情報を取得
		for(int cc=0; cc< testDB.GetColumnNum(); cc++){
			System.out.println("column "+cc+"'s flug AllowNull : "+testDB.checkAllowNull(cc));
		}

		/*==============DBの情報を取得==================*/
		System.out.println("DB Name is \""+testDB.GetDBName()+"\"");
		System.out.println("DB Size is \""+testDB.GetDBSize()+"\"");
		System.out.println("");


		/* =============Recodeの追加============== */
		//Recode数を取得
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");

		//Insert new Recode
		String[] insertedRecode = {"data0","data1","data2","data3","data4","data5","data6"};
		result = testDB.Insert(insertedRecode);
		System.out.println("result of Insert : "+result);

		//Recode数を取得
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");

		//カラム数に合わないレコードをInsertして失敗させる
		String[] insertedRecode2 = {"data0","data1"};
		result = testDB.Insert(insertedRecode2);
		System.out.println("result of Insert : "+result);

		//Recode数を取得
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");

		//カラム数に合わないレコードをInsertして失敗させる
		String[] insertedRecode3 = {"data0","data1","data2","data3","data4","data5","data6","data7","data8"};
		result = testDB.Insert(insertedRecode3);
		System.out.println("result of Insert : "+result);

		//Recode数を取得
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");

		//Insert new Recode(全くダブらない新しいデータ)
		String[] insertedRecode4 = {"data7","data8","data9","data10","data11","data12","data13"};
		result = testDB.Insert(insertedRecode4);
		System.out.println("result of Insert : "+result);


		//Insert new Recode(完全にダブり)
		String[] insertedRecode5 = {"data0","data1","data2","data3","data4","data5","data6"};
		result = testDB.Insert(insertedRecode5);
		System.out.println("result of Insert : "+result);

		//Insert new Recode(部分的にダブるデータ)
		String[] insertedRecode6 = {"data7","data8","data9","data10","data14","data15","data16"};
		result = testDB.Insert(insertedRecode6);
		System.out.println("result of Insert : "+result);

		//Insert new Recode(nullを許すところにnullを入れたデータ)
		String[] insertedRecode7 = {"data17","","data18","","data19","","data20"};
		result = testDB.Insert(insertedRecode7);
		System.out.println("result of Insert : "+result);

		//Insert new Recode(nullを許さないところにnullを入れたデータ)
		String[] insertedRecode8 = {"","data21","","data22","","data23",""};
		result = testDB.Insert(insertedRecode8);
		System.out.println("result of Insert : "+result);
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");



		/*==============recodeの削除==================*/
		//Delete Recode
		System.out.println("now Recode 0 : \""+testDB.Select(0)+"\"");
		result = testDB.Delete(0);
		System.out.println("result of Delete Line0 : "+result);
		System.out.println("now Recode 0 : \""+testDB.Select(0)+"\"");
		System.out.println("now Recode num : "+testDB.GetRecodeNum());
		System.out.println("");


		/*==============recode/dataの取得==================*/
		//全件Recode取得
		String[] AllRecode = testDB.Select();
		System.out.println("is this true? "+testDB.GetRecodeNum() +"="+AllRecode.length);
		System.out.println("all recodes is follow.");
		for(int rc=0; rc<AllRecode.length; rc++){
			System.out.println("\t"+AllRecode[rc]);
		}
		System.out.println("");

		//Recode 0 を丸々取得
		System.out.println("now Recode 0 : \""+testDB.Select(0)+"\"");
		System.out.println("");

		//Recode 0 の第0カラムを取得
		System.out.println("recode0,column0 : \""+testDB.SelectData(0,0)+"\"");
		//Recode 0 の第1カラムを取得
		System.out.println("recode0,column1 : \""+testDB.SelectData(0,1)+"\"");
		//Recode 0 の第2カラムを取得
		System.out.println("recode0,column2 : \""+testDB.SelectData(0,2)+"\"");
		//Recode 0 の第3カラムを取得
		System.out.println("recode0,column3 : \""+testDB.SelectData(0,3)+"\"");
		//Recode 0 の第4カラムを取得
		System.out.println("recode0,column4 : \""+testDB.SelectData(0,4)+"\"");
		//Recode 0 の第5カラムを取得
		System.out.println("recode0,column5 : \""+testDB.SelectData(0,5)+"\"");
		//Recode 0 の第6カラムを取得
		System.out.println("recode0,column6 : \""+testDB.SelectData(0,6)+"\"");
		System.out.println("");

		//Recode 1 を丸々取得
		System.out.println("now Recode 1 : \""+testDB.Select(1)+"\"");
		System.out.println("");

		//Recode 1 の第0カラムを取得
		System.out.println("recode1,column0 : \""+testDB.SelectData(1,0)+"\"");
		//Recode 1 の第1カラムを取得
		System.out.println("recode1,column1 : \""+testDB.SelectData(1,1)+"\"");
		//Recode 1 の第2カラムを取得
		System.out.println("recode1,column2 : \""+testDB.SelectData(1,2)+"\"");
		//Recode 1 の第3カラムを取得
		System.out.println("recode1,column3 : \""+testDB.SelectData(1,3)+"\"");
		//Recode 1 の第4カラムを取得
		System.out.println("recode1,column4 : \""+testDB.SelectData(1,4)+"\"");
		//Recode 1 の第5カラムを取得
		System.out.println("recode1,column5 : \""+testDB.SelectData(1,5)+"\"");
		//Recode 1 の第6カラムを取得
		System.out.println("recode1,column6 : \""+testDB.SelectData(1,6)+"\"");
		System.out.println("");

		//column0が"data0"であるrecodeのIDを探す
		int[] matchRecodeID = testDB.SelectID(0,"data0");
		System.out.println("column0 is \"data0\" RecodeID is follow.");
		for(int rc=0; rc<matchRecodeID.length;rc++){
			System.out.println("\t"+matchRecodeID[rc]);
		}
		System.out.println("");

		//column0が"data0"であるrecodeを丸々取得
		String[] matchRecode = testDB.Select(0,"data0");
		System.out.println("column0 is \"data0\" Recode is follow.");
		for(int rc=0; rc<matchRecode.length;rc++){
			System.out.println("\t"+matchRecode[rc]);
		}
		System.out.println("");


		//全recodeのcolumn3を取得
		String[] allColumn3 = testDB.SelectData(3);
		System.out.println("all column3 is follow.");
		for(int rc=0; rc<allColumn3.length;rc++){
			System.out.println("\t\""+allColumn3[rc]+"\"");
		}
		System.out.println("");

		//column0が"data0"であるrecodeのcolumn3を取得
		String[] matchColumn3 = testDB.SelectData(0,"data0",3);
		System.out.println("column0 is \"data0\" column3 is follow.");
		for(int rc=0; rc<matchColumn3.length;rc++){
			System.out.println("\t"+matchColumn3[rc]);
		}
		System.out.println("");


		//================Update====================
		testDB.Update(0,"column1","This is End of Test.");

	}

}
