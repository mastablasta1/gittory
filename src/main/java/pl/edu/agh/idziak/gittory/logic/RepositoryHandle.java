package pl.edu.agh.idziak.gittory.logic;

import org.eclipse.jgit.lib.Repository;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepositoryHandle {
    private Repository repository;
    private String name;

    public RepositoryHandle(Repository repository) {
        this.repository = repository;
        name = repository.getWorkTree().getName();
    }


    public Repository getRepository() {
        return repository;
    }

    public String getName() {
        return name;
    }
}
