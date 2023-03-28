package server.configs;

import models.Board;
import models.TaskCard;
import models.TaskList;
import models.Workspace;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repositories.BoardRepository;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;
import server.repositories.WorkspaceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Configuration
public class H2MemConfig {
    private String generatePassword() {
        int charA = 97;
        int charZ = 122;

        Random random = new Random();
        StringBuilder buffer = new StringBuilder();
        int length = random.nextInt(8) + 8;
        for (int i = 0; i < length; i++) {
            int code = random.nextInt(charZ - charA + 1) + charA;
            buffer.append((char) code);
        }

        return buffer.toString();
    }

    @Bean
    CommandLineRunner commandLineRunner(BoardRepository boardRepository,
                                        TaskListRepository taskListRepository,
                                        TaskCardRepository taskCardRepository,
                                        WorkspaceRepository workspaceRepository) {
        return args -> {
            Board board1 = new Board("Board1");
            Board board2 = new Board("Board2");
            Board board3 = new Board("Board3");
            String password = generatePassword();
            workspaceRepository.save(new Workspace(password));
            System.out.println("Generated password is: " + password);
            boardRepository.saveAll(List.of(board1, board2, board3));
            for (int i = 0; i < 6; i++) {
                Random random = new Random();
                TaskList taskList = new TaskList(String.valueOf(random.nextInt(1000, 9999)), board1);
                board1.getTaskLists().add(taskList);
                taskListRepository.save(taskList);
                for (int j = 0; j < 5; j++) {
                    TaskCard taskCard = new TaskCard(String.valueOf(random.nextInt(1000, 9999)), taskList, j);
                    taskList.getTaskCards().add(taskCard);

                    Map<String, Boolean> a = new HashMap<>();
                    a.put("Task 1", false);
                    a.put("Task 2", true);
                    taskCard.setSubs(a);

                    taskCardRepository.save(taskCard);
                }
            }
        };
    }
}
