package SmallDB.EditedFile;

import SmallDB.EditedFile.EditedFile;

public class EditedFile_test{

	public static void main(String[] args){
		//instanceの作成, constructorのテスト
		EditedFile ef = new EditedFile("./testEditedFile.data");

		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//行を挿入
		int result = 0;
		result = ef.InsertLine(0, "This is test LineMessage. (Line 0.0)");
		System.out.println("InsertLine 0.0 Result : "+result);
	
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//行を挿入
		result = ef.InsertLine(0, "This is test LineMessage. (Line 0.1)");
		System.out.println("InsertLine 0.1 Result : "+result);
		
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//行を挿入
		result = ef.InsertLine(100, "This is test LineMessage. (Line 100.0)");
		System.out.println("InsertLine 100.0 Result : "+result);
		
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//行を挿入
		result = ef.InsertLine(1, "This is test LineMessage. (Line 1.0)");
		System.out.println("InsertLine 1.0 Result : "+result);
			
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());


		//行削除
		result = ef.DeleteMatchLine(1);
		System.out.println("DeleteMatchLine 1.0 Result : "+result);
	
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//行をアップデート
		result = ef.UpdateLine(0,"This is test Update Line 0.0");
		System.out.println("UpdateLine 0.0 Result : "+result);
		
		//全行数を取得
		System.out.println("now All Line Num : "+ef.SearchAllLineNum());

		//0行目のStringを取得
		System.out.println("Search Line : \""+ef.GetMatchLine(0)+"\"");
	}
}



