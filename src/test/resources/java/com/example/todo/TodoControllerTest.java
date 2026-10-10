package com.example.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private Todo saveTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        return repository.save(todo);
    }

    @Test
    void create_returns201() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Learn Jenkins\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn Jenkins"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void getAll_returnsSavedTodos() throws Exception {
        saveTodo("Task one");
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task one"));
    }

    @Test
    void toggle_flipsDoneFlag() throws Exception {
        Todo saved = saveTodo("Task two");
        mockMvc.perform(put("/api/todos/" + saved.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void toggle_unknownId_returns404() throws Exception {
        mockMvc.perform(put("/api/todos/9999/toggle"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        Todo saved = saveTodo("Task three");
        mockMvc.perform(delete("/api/todos/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
