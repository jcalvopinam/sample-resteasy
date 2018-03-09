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

package com.jcalvopinam.dao;

import com.jcalvopinam.model.User;
import com.jcalvopinam.repository.DataInMemoryDao;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jcalvopinam.model.User.Gender.FEMALE;
import static com.jcalvopinam.model.User.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

public class DataInMemoryDaoTest {

    private DataInMemoryDao dataInMemoryDao;

    @Before
    public void setUp() throws Exception {
        dataInMemoryDao = new DataInMemoryDao();
    }

    @Test
    public void shouldSelectAllUsers() {
        List<User> users = dataInMemoryDao.selectAllUsers();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getAge()).isEqualTo(28);
        assertThat(user.getEmail()).isEqualTo("phillip.j.fry@gmail.com");
        assertThat(user.getFirstName()).isEqualTo("Phillip J.");
        assertThat(user.getLastName()).isEqualTo("Fry");
        assertThat(user.getGender()).isEqualTo(MALE);
        assertThat(user.getUserId()).isNotNull();
    }

    @Test
    public void shouldSelectUserByUserId() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Turanga", "Leela", FEMALE, 30, "turanga.leela@gmail.com");
        dataInMemoryDao.insertUser(userId, user);
        assertThat(dataInMemoryDao.selectAllUsers()).hasSize(2);
        Optional<User> optionalUser = dataInMemoryDao.selectUserByUserId(userId);
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get()).isEqualToComparingFieldByField(user);
    }

    @Test
    public void shouldNotSelectUserByRandomUserId() {
        Optional<User> optionalUser = dataInMemoryDao.selectUserByUserId(UUID.randomUUID());
        assertThat(optionalUser.isPresent()).isFalse();
    }

    @Test
    public void shouldUpdateUser() {
        UUID fryUserId = dataInMemoryDao.selectAllUsers().get(0).getUserId();
        User newUser = new User(fryUserId, "Turanga", "Leela", FEMALE, 30, "turanga.leela@gmail.com");
        dataInMemoryDao.updateUser(newUser);
        Optional<User> optionalUser = dataInMemoryDao.selectUserByUserId(fryUserId);
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(dataInMemoryDao.selectAllUsers()).hasSize(1);
        assertThat(optionalUser.get()).isEqualToComparingFieldByField(newUser);
    }

    @Test
    public void shouldDeleteUserByUserId() {
        UUID fryUserId = dataInMemoryDao.selectAllUsers().get(0).getUserId();
        dataInMemoryDao.deleteUserByUserId(fryUserId);
        assertThat(dataInMemoryDao.selectUserByUserId(fryUserId).isPresent()).isFalse();
        assertThat(dataInMemoryDao.selectAllUsers().isEmpty());
    }

    @Test
    public void shouldInsertUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Turanga", "Leela", FEMALE, 18, "turanga.leela@gmail.com");
        dataInMemoryDao.insertUser(userId, user);
        List<User> users = dataInMemoryDao.selectAllUsers();
        assertThat(users).hasSize(2);
        assertThat(dataInMemoryDao.selectUserByUserId(userId).get()).isEqualToComparingFieldByField(user);
    }
}