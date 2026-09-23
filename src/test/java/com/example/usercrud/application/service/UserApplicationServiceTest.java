package com.example.usercrud.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.usercrud.application.port.in.CreateUserCommand;
import com.example.usercrud.application.port.out.UserRepositoryPort;
import com.example.usercrud.domain.exception.DuplicateEmailException;
import com.example.usercrud.domain.exception.UserNotFoundException;
import com.example.usercrud.domain.model.Email;
import com.example.usercrud.domain.model.User;
import com.example.usercrud.domain.model.UserId;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserApplicationService service;

    @Test
    void createOk() {
        var command = new CreateUserCommand("Juan", "juan@example.com", true);
        when(userRepositoryPort.existsByEmail("juan@example.com")).thenReturn(false);
        var saved = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.save(any(User.class))).thenReturn(saved);

        var result = service.create(command);

        assertThat(result.getId().value()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Juan");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void createConActivoNullAsignaTrue() {
        var command = new CreateUserCommand("Juan", "juan@example.com", null);
        when(userRepositoryPort.existsByEmail("juan@example.com")).thenReturn(false);
        var saved = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.save(any(User.class))).thenReturn(saved);

        service.create(command);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isActivo()).isTrue();
    }

    @Test
    void createEmailDuplicadoLanzaExcepcionYNoGuarda() {
        var command = new CreateUserCommand("Juan", "juan@example.com", true);
        when(userRepositoryPort.existsByEmail("juan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void findAll() {
        var user = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.findAll()).thenReturn(List.of(user));

        var result = service.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByIdExistente() {
        var user = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.findById(new UserId(1L))).thenReturn(Optional.of(user));

        var result = service.findById(1L);

        assertThat(result.getId().value()).isEqualTo(1L);
    }

    @Test
    void findByIdInexistenteLanzaExcepcion() {
        when(userRepositoryPort.findById(new UserId(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateOk() {
        var existente = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.findById(new UserId(1L))).thenReturn(Optional.of(existente));
        var command = new CreateUserCommand("Juan Modificado", "juan@example.com", false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.update(1L, command);

        assertThat(result.getNombre()).isEqualTo("Juan Modificado");
        assertThat(result.isActivo()).isFalse();
    }

    @Test
    void updateInexistenteLanzaExcepcion() {
        when(userRepositoryPort.findById(new UserId(99L))).thenReturn(Optional.empty());
        var command = new CreateUserCommand("Juan", "juan@example.com", true);

        assertThatThrownBy(() -> service.update(99L, command))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateConEmailDeOtroUsuarioLanzaExcepcion() {
        var existente = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.findById(new UserId(1L))).thenReturn(Optional.of(existente));
        when(userRepositoryPort.existsByEmail("otro@example.com")).thenReturn(true);
        var command = new CreateUserCommand("Juan", "otro@example.com", true);

        assertThatThrownBy(() -> service.update(1L, command))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void updateConMismoEmailNoConsultaExistsByEmail() {
        var existente = User.reconstituir(new UserId(1L), "Juan", new Email("juan@example.com"), true);
        when(userRepositoryPort.findById(new UserId(1L))).thenReturn(Optional.of(existente));
        when(userRepositoryPort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        var command = new CreateUserCommand("Juan Nuevo", "juan@example.com", true);

        service.update(1L, command);

        verify(userRepositoryPort, never()).existsByEmail(any());
    }

    @Test
    void deleteOk() {
        when(userRepositoryPort.existsById(new UserId(1L))).thenReturn(true);

        service.delete(1L);

        verify(userRepositoryPort, times(1)).deleteById(new UserId(1L));
    }

    @Test
    void deleteInexistenteLanzaExcepcion() {
        when(userRepositoryPort.existsById(new UserId(99L))).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepositoryPort, never()).deleteById(any());
    }
}
