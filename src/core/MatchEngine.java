package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchEngine implements Serializable{
	private List<Tag> tags;
	
	public MatchEngine() {
		tags = new ArrayList<Tag>();
	}
	
	public Tag match(Article article){
		Tag tag = null;
		double highest = 0, tmp;
		
		for(Tag t : tags){
			tmp = t.getConsistency(article);
			if(tmp > highest){
				tag = t;
				highest = tmp;
			}
		}
		//update matcher with new valid article
		tag.updateTag(article);
		return tag;
	}
	
	public void addTag(Tag tag){
		tags.add(tag);
	}
	
	public void addDataSet(Article article, Tag tag){
		Criteria crit;
		for(Spec s : article.getSpecs()){
			tag.addSpec(s);
		}
	}
	
	public Tag getTag(String tag){
		
		for(Tag t : tags){
			if(t.getName().equals(tag)){
				return t;
			}
		}
		return null;
	}
	
}	
