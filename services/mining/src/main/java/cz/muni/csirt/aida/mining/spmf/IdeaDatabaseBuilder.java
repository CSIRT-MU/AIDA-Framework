package cz.muni.csirt.aida.mining.spmf;

import cz.muni.csirt.aida.idea.Idea;

interface IdeaDatabaseBuilder<T> {

	void addEvent(Idea idea);

	T build();
}
