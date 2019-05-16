package cz.vitek.bakalarium.Utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import cz.vitek.bakalarium.POJOs.Homework;

public class HomeworkDiffCallback extends DiffUtil.Callback {
    private List<Homework> oldHomework;
    private List<Homework> newHomework;

    public HomeworkDiffCallback(List<Homework> oldHomework, List<Homework> newHomework) {
        this.oldHomework = oldHomework;
        this.newHomework = newHomework;
    }

    @Override
    public int getOldListSize() {
        return oldHomework.size();
    }

    @Override
    public int getNewListSize() {
        return newHomework.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldHomework.get(oldItemPosition).getId().equals(newHomework.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
