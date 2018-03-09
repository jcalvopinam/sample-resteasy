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

package com.jcalvopinam.it;

import com.jcalvopinam.config.proxy.UserControllerV1;
import com.jcalvopinam.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;

import static com.jcalvopinam.model.User.Gender.FEMALE;
import static com.jcalvopinam.model.User.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserIntegrationTest {

    @Autowired
    private UserControllerV1 userControllerV1;

    @Test
    public void itShouldRetrieveAllUsers() {
        List<User> users = userControllerV1.retrieveUsers(null);
        assertThat(users).hasSize(4);
        assertThat(users.get(0).getUserId()).isInstanceOf(UUID.class);
        assertThat(users.get(0).getUserId()).isNotNull();
    }

    @Test
    public void shouldInsertUser() {
        User user = this.createNewUser();
        userControllerV1.insertNewUser(user);
        User retrieveUser = userControllerV1.retrieveUser(user.getUserId());
        assertThat(user).isEqualToComparingFieldByField(retrieveUser);
    }

    @Test
    public void shouldDeleteUser() {
        User user = this.createNewUser();
        userControllerV1.insertNewUser(user);
        User retrieveUser = userControllerV1.retrieveUser(user.getUserId());
        assertThat(user).isEqualToComparingFieldByField(retrieveUser);

        userControllerV1.deleteUser(user.getUserId());
        assertThatThrownBy(() -> userControllerV1.retrieveUser(user.getUserId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldUpdateUser() {
        User user = this.createNewUser();
        userControllerV1.insertNewUser(user);

        User updateUser = new User(user.getUserId(), "Hubert", "Farnsworth", MALE, 180, "hubert.farnsworth@futurama.com");
        User retrieveUser = userControllerV1.updateUser(updateUser);

        assertThat(retrieveUser).isEqualToComparingFieldByField(updateUser);
    }

    @Test
    public void shouldRetrieveUsersByGender() {
        User user = this.createNewUser();
        userControllerV1.insertNewUser(user);

        List<User> maleUsers = userControllerV1.retrieveUsers(MALE.name());
        assertThat(maleUsers).extracting("gender").doesNotContain(user.getUserId());

        List<User> femaleUsers = userControllerV1.retrieveUsers(FEMALE.name());
        assertThat(femaleUsers).extracting("gender").contains(user.getGender());
        assertThat(femaleUsers.get(0).getGender().name()).isEqualTo(FEMALE.name());
    }

    private User createNewUser() {
        UUID userId = UUID.randomUUID();
        return new User(userId, "Turanga", "Leela", FEMALE, 30, "turanga.leela@gmail.com");
    }
}
