package pl.edu.agh.idziak.gittory.logic;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepositoryService {

    private List<RepositoryHandle> repositories = new ArrayList<>();
    private Observable<Event> observable = new Observable<>();

    public void addRepository(RepositoryHandle repo){
        repositories.add(repo);
        Event event = Event.builder().type(Event.Type.REPOSITORIES_CHANGED).build();
        observable.publishEvent(event);
    }

    public List<RepositoryHandle> getRepositories() {
        return new ArrayList<>(repositories);
    }

    public static RepositoryHandle loadGitRepository(File repoDir) throws IOException {
        FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();
        fileRepositoryBuilder.setMustExist(true);
        fileRepositoryBuilder.setWorkTree(repoDir);
        Repository repo = fileRepositoryBuilder.build();
        return new RepositoryHandle(repo);
    }

    public void addChangeListener(Consumer<Event> consumer) {
        observable.addObserver(consumer);
    }
}
