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

package com.jcalvopinam.repository;

import com.jcalvopinam.dao.UserDao;
import com.jcalvopinam.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.jcalvopinam.model.User.Gender.MALE;

@Repository
public class DataInMemoryDao implements UserDao {

    private Map<UUID, User> database;

    public DataInMemoryDao() {
        database = new HashMap<>();
        UUID userId = UUID.randomUUID();
        database.put(userId, new User(userId, "Phillip J.", "Fry", MALE, 28, "phillip.j.fry@gmail.com"));
    }

    @Override
    public List<User> selectAllUsers() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Optional<User> selectUserByUserId(UUID userId) {
        return Optional.ofNullable(database.get(userId));
    }

    @Override
    public User updateUser(User user) {
        database.put(user.getUserId(), user);
        return selectUserByUserId(user.getUserId()).get();
    }

    @Override
    public int deleteUserByUserId(UUID userId) {
        database.remove(userId);
        return 1;
    }

    @Override
    public User insertUser(UUID userId, User user) {
        database.put(userId, user);
        return selectUserByUserId(userId).get();
    }

}
