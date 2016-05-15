package pl.edu.agh.idziak.gittory.logic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class JavaCodeProcessor {

    public static List<Node> getChildNodesInOrder(CompilationUnit compilationUnit) {
        List<Node> nodeList = new LinkedList<>();
        nodeList.add(compilationUnit);
        boolean nodeWithChildrenFound;

        do {
            nodeWithChildrenFound = false;
            List<Node> childrenList = new LinkedList<>();
            for (Node currentNode : nodeList) {
                List<Node> children = currentNode.getChildrenNodes();
                if (children.isEmpty()) {
                    childrenList.add(currentNode);
                } else {
                    childrenList.addAll(children);
                    nodeWithChildrenFound = true;
                }
            }
            nodeList = childrenList;
        } while (nodeWithChildrenFound);

        return nodeList;
    }
}
