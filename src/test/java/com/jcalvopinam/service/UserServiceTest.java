/*
 * MIT License
 *
 * Copyright (c) 2018 JUAN CALVOPINA M
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jcalvopinam.service;

import com.jcalvopinam.model.User;
import com.jcalvopinam.repository.DataInMemoryDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.jcalvopinam.model.User.Gender.FEMALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private DataInMemoryDao dataInMemoryDao;

    private UserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(dataInMemoryDao);
    }

    @Test
    public void shouldGetAllUsers() {
        User user = this.createUser();
        List<User> users = new ArrayList<>(Arrays.asList(user));

        when(dataInMemoryDao.selectAllUsers()).thenReturn(users);
        List<User> allUsers = userService.getAllUsers(Optional.empty());
        assertThat(allUsers).hasSize(1);

        User currentUser = users.get(0);
        this.assertUserFields(currentUser);
    }

    @Test
    public void shouldGetUser() {
        User user = this.createUser();
        given(dataInMemoryDao.selectUserByUserId(user.getUserId())).willReturn(Optional.of(user));

        Optional<User> userOptional = userService.getUser(user.getUserId());
        assertThat(userOptional.isPresent()).isTrue();

        User currentUser = userOptional.get();
        this.assertUserFields(currentUser);
    }

    @Test
    public void shouldUpdateUser() {
        User user = this.createUser();
        given(dataInMemoryDao.selectUserByUserId(user.getUserId())).willReturn(Optional.of(user));
        given(dataInMemoryDao.updateUser(user)).willReturn(user);

        User updateUser = userService.updateUser(user);
        verify(dataInMemoryDao).selectUserByUserId(user.getUserId());
        assertThat(updateUser.getUserId()).isEqualTo(user.getUserId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(dataInMemoryDao).updateUser(captor.capture());
        User captorValue = captor.getValue();
        assertUserFields(captorValue);
    }

    @Test
    public void shouldRemoveUser() {
        User user = this.createUser();
        given(dataInMemoryDao.selectUserByUserId(user.getUserId())).willReturn(Optional.of(user));
        given(dataInMemoryDao.deleteUserByUserId(user.getUserId())).willReturn(1);

        int deleteUser = userService.removeUser(user.getUserId());
        verify(dataInMemoryDao).selectUserByUserId(user.getUserId());
        verify(dataInMemoryDao).deleteUserByUserId(user.getUserId());

        assertThat(deleteUser).isEqualTo(1);
    }

    @Test
    public void insertUser() {
        User user = this.createUser();

        when(dataInMemoryDao.insertUser(any(UUID.class), any(User.class))).thenReturn(user);
        User insertUser = userService.insertUser(user);
        assertThat(insertUser.getUserId()).isEqualTo(user.getUserId());

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(dataInMemoryDao).insertUser(any(UUID.class), argumentCaptor.capture());
        User userValue = argumentCaptor.getValue();
        assertUserFields(userValue);
    }

    private User createUser() {
        UUID userId = UUID.randomUUID();
        return new User(userId, "Turanga", "Leela", FEMALE, 30, "turanga.leela@gmail.com");
    }

    private void assertUserFields(User currentUser) {
        assertThat(currentUser.getAge()).isEqualTo(30);
        assertThat(currentUser.getEmail()).isEqualTo("turanga.leela@gmail.com");
        assertThat(currentUser.getFirstName()).isEqualTo("Turanga");
        assertThat(currentUser.getLastName()).isEqualTo("Leela");
        assertThat(currentUser.getGender()).isEqualTo(FEMALE);
        assertThat(currentUser.getUserId()).isNotNull();
    }

}