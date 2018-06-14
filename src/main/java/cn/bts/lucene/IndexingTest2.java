package cn.bts.lucene;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
* @author stevenxy E-mail:random_xy@163.com
* @Date 2018年6月13日
* @Description 
*/
public class IndexingTest2 {
	private Directory dir;
	private String ids[]={"1","2","3","4"};
	private String authors[]={"Jack","Marry","John","Json"};
	private String positions[]={"accounting","technician","salesperson","boss"};
	private String titles[]={"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
	private String contents[]={
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};
	
	/**
	 * 获取IndexWriter实例
	 * @return
	 * @throws Exception
	 */
	private IndexWriter getWriter()throws Exception{
		Analyzer analyzer=new StandardAnalyzer();
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		IndexWriter indexWriter=new IndexWriter(dir,iwc);
		return indexWriter;
	}
	
	/**
	 * 生成索引 加权操作
	 * @throws Exception
	 */
	@Test
	public void index()throws Exception{
		dir=FSDirectory.open(Paths.get("D:\\lucene3"));
		IndexWriter writer=getWriter();
		for(int i=0;i<ids.length;i++) {
			Document document=new Document();
			document.add(new StringField("id",ids[i],Field.Store.YES));
			document.add(new StringField("author",authors[i],Field.Store.YES));
			document.add(new StringField("position",positions[i],Field.Store.YES));
			/*加权操作*/
			TextField field=new TextField("title",titles[i],Field.Store.YES);
			if("boss".equals(positions[i])) {
				field.setBoost(1.5f);
			}
			document.add(field);
			document.add(new TextField("content",contents[i],Field.Store.NO));
			writer.addDocument(document);
		}
			writer.close();
	}
	/**
	 * 查询
	 * @throws Exception
	 */
	@Test
	public void search()throws Exception{
		dir=FSDirectory.open(Paths.get("D:\\lucene3"));
		IndexReader reader=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(reader);
		String searchField="title";
		String q="java";
		Term term=new Term(searchField, q);
		Query query=new TermQuery(term);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配'"+q+"',总共查询到"+hits.totalHits+"个文档");
		for(ScoreDoc scoreDocs:hits.scoreDocs) {
			Document document=is.doc(scoreDocs.doc);
			System.out.println(document.get("author"));
		}
		reader.close();
	}
	
}
