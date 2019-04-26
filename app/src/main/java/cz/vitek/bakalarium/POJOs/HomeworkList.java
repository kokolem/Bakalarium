package cz.vitek.bakalarium.POJOs;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class HomeworkList {

    @ElementList(name = "ukoly", entry = "ukol")
    private List<Homework> list;

    public HomeworkList() {
    }

    public List<Homework> getList() {
        return list;
    }

    public void setList(List<Homework> list) {
        this.list = list;
    }
}
