package server.controllers;

import models.Workspace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.repositories.WorkspaceRepository;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceRepository repo;

    /**
     * Constructor Method
     *
     * @param repo The injected repository of the object
     */
    public WorkspaceController(WorkspaceRepository repo) {
        this.repo = repo;
    }

    /**
     * Get password Method
     *
     * @return Generated password
     */
    @GetMapping("")
    public Workspace getAll() {
        return repo.findAll().get(0);
    }
}
