package pl.edu.agh.idziak.gittory.logic;

import org.eclipse.jgit.lib.Repository;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepositoryHandle {
    private Repository repository;

    public RepositoryHandle(Repository repository) {
        this.repository = repository;
    }


    public Repository getRepository() {
        return repository;
    }
}
