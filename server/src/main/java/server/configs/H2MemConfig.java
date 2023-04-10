package server.configs;


import models.Board;
import models.Tag;
import models.TaskCard;
import models.TaskList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repositories.BoardRepository;
import server.repositories.TagRepository;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;

import java.util.*;

@Configuration
public class H2MemConfig {

    @Bean
    CommandLineRunner commandLineRunner(BoardRepository boardRepository,
                                        TaskListRepository taskListRepository,
                                        TaskCardRepository taskCardRepository, TagRepository tagRepository) {
        return args -> {

            Board board1 = new Board("Board1");
            Board board2 = new Board("Board2");
            Board board3 = new Board("Board3");

            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("gaming", board1, "#000000"));
            tags.add(new Tag("homework >:)", board1, "#FF0000"));
            tags.add(new Tag("new", board1, "#0000FF"));

            board1.setTags(tags);

            List<Board> saved = boardRepository.saveAll(List.of(board1, board2, board3));
            System.out.println(saved.get(0).getId());
            System.out.println(saved.get(1).getId());
            System.out.println(saved.get(2).getId());
            tagRepository.saveAll(tags);
            for (int i = 0; i < 6; i++) {
                Random random = new Random();
                TaskList taskList = new TaskList(String.valueOf(random.nextInt(1000, 9999)), board1);

                board1.getTaskLists().add(taskList);
                taskListRepository.save(taskList);
                for (int j = 0; j < 5; j++) {
                    TaskCard taskCard = new TaskCard(String.valueOf(random.nextInt(1000, 9999)), taskList, j);
                    taskCard.setBackID("rgb(" + random.nextInt(0, 256) + "," + random.nextInt(0, 256) + ","
                            + random.nextInt(0, 256) + ")");
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
