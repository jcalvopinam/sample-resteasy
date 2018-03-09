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

import com.jcalvopinam.dao.UserDao;
import com.jcalvopinam.model.User;
import com.jcalvopinam.model.User.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers(Optional<String> gender) {
        List<User> users = userDao.selectAllUsers();
        if (!gender.isPresent()) {
            return users;
        }
        try {
            Gender obtainGender = User.Gender.valueOf(gender.get().toUpperCase());
            return users.stream()
                    .filter(user -> user.getGender().equals(obtainGender))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid gender", e);
        }
    }

    public User updateUser(User user) {
        Optional<User> optionalUser = this.getUser(user.getUserId());
        if (optionalUser.isPresent()) {
            return userDao.updateUser(user);
        }
        throw new NotFoundException("user " + user.getUserId() + " not found");
    }

    public int removeUser(UUID userId) {
        UUID userUid = this.getUser(userId)
                .map(User::getUserId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found"));
        return userDao.deleteUserByUserId(userUid);
    }

    public User insertUser(User user) {
        UUID userUid = user.getUserId() == null ? UUID.randomUUID() : user.getUserId();
        User newUser = User.newUser(userUid, user);
        return userDao.insertUser(userUid, newUser);
    }

    public Optional<User> getUser(UUID userId) {
        return userDao.selectUserByUserId(userId);
    }
}
