package com.example.usercrud.infrastructure.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.usercrud.application.port.in.CreateUserCommand;
import com.example.usercrud.application.port.in.CreateUserUseCase;
import com.example.usercrud.application.port.in.DeleteUserUseCase;
import com.example.usercrud.application.port.in.GetUserUseCase;
import com.example.usercrud.application.port.in.UpdateUserUseCase;
import com.example.usercrud.domain.exception.DuplicateEmailException;
import com.example.usercrud.domain.exception.UserNotFoundException;
import com.example.usercrud.domain.model.Email;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

@WebMvcTest(UserController.class)
@Import(UserWebMapper.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;

    private User sampleUser() {
        return User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
    }

    @Test
    void postValidoDevuelve201ConLocation() throws Exception {
        when(createUserUseCase.create(any(CreateUserCommand.class))).thenReturn(sampleUser());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@example.com\",\"activo\":true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    void postInvalidoDevuelve400ConErrors() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"no-es-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void postDuplicadoDevuelve409() throws Exception {
        when(createUserUseCase.create(any(CreateUserCommand.class)))
                .thenThrow(new DuplicateEmailException("El email ya está registrado: juan@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@example.com\",\"activo\":true}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getListaDevuelve200() throws Exception {
        when(getUserUseCase.findAll()).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getPorIdDevuelve200() throws Exception {
        when(getUserUseCase.findById(1L)).thenReturn(sampleUser());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPorIdInexistenteDevuelve404() throws Exception {
        when(getUserUseCase.findById(99L))
                .thenThrow(new UserNotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void putDevuelve200() throws Exception {
        when(updateUserUseCase.update(eq(1L), any(CreateUserCommand.class)))
                .thenReturn(User.reconstituir(new UserId(1L), "Juan Mod", new Email("juan@example.com"), true));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan Mod\",\"email\":\"juan@example.com\",\"activo\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Mod"));
    }

    @Test
    void putInexistenteDevuelve404() throws Exception {
        when(updateUserUseCase.update(eq(99L), any(CreateUserCommand.class)))
                .thenThrow(new UserNotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@example.com\",\"activo\":true}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDevuelve204() throws Exception {
        doNothing().when(deleteUserUseCase).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteInexistenteDevuelve404() throws Exception {
        doThrow(new UserNotFoundException("Usuario no encontrado con id: 99"))
                .when(deleteUserUseCase).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
