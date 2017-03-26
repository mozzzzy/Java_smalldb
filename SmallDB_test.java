import SmallDB.SmallDB;

class SmallDB_test{

	public static void main(String[] args){

		//DBの作成。引数はファイルパス。(既にあったら何もしない)
		SmallDB sampleDB1 = new SmallDB("./sampleDB1.smdb");

		//Columnを作成
		String[] columns1 = {"name","date"};
		sampleDB1.AddColumns(columns1);

		//Columnを追加
		String[] columns2 = {"owner"};
		sampleDB1.AddColumns(columns2);

		//Recodeを入れる
		String[] Data0 = {"test0","20161014120010","ken"};
		String[] Data1 = {"test1","20161014120011","john"};
		String[] Data2 = {"test2","20161014120012",null};
		sampleDB1.Insert(Data0);
		sampleDB1.Insert(Data1);
		sampleDB1.Insert(Data2);


		//DB名の取得
		String DBName = sampleDB1.GetDBName();
		System.out.println("[sample code] DBName = "+DBName);

		//Column数を取得
		int ColumnNum = sampleDB1.GetColumnNum();
		System.out.println("[sample code] ColumnNum = "+ColumnNum);

		//Columnの文字列を取得
		String[] Columns = sampleDB1.GetColumns();
		System.out.println("[sample code] Column = ");
		for(int cc=0;cc<Columns.length;cc++){
			System.out.println("\t["+cc+"] "+Columns[cc]);
		}

		//それぞれのColumnのColumnIDを取得
		int[] ColumnID = new int[ColumnNum];
		for(int cc=0;cc<Columns.length;cc++){
			ColumnID[cc] = sampleDB1.GetColumnID(Columns[cc]);
		}
		System.out.println("[sample code] EachColumn's ID = ");
		for(int cc=0;cc<ColumnID.length;cc++){
			System.out.println("\t"+Columns[cc]+":"+ColumnID[cc]);
		}

		//データの全件取得
		String[] AllData = sampleDB1.Select();
		System.out.println("[sample code] AllData = ");
		for(int dc=0;dc<AllData.length;dc++){
			System.out.println("\t["+dc+"] "+AllData[dc]);
		}

		//name Columnが"john"のデータを取得
		String[] SelectedData = sampleDB1.Select("name","john");
		System.out.println("[sample code] SelectedData = ");
		for(int dc=0;dc<SelectedData.length;dc++){
			System.out.println("\t["+dc+"] "+SelectedData[dc]);
		}

		//nameが"john"のデータidを取得
		int[] SelectedDataID = sampleDB1.SelectID("name","john");
		System.out.println("[sample code] SelectedData ID = ");
		for(int dc=0;dc<SelectedDataID.length;dc++){
			System.out.println("\t["+dc+"] "+SelectedDataID[dc]);
		}




		//Recode id 0 のデータを取得
		String Recode_0 = sampleDB1.Select(0);
		System.out.println("[sample code] Recode_0 = \n\t"+Recode_0);

		//データ更新(id 0のデータのownerカラムを"misa"に変更)
		sampleDB1.Update(0,"owner","misa");

		//データ削除
		sampleDB1.Delete(2);
	}	
}
