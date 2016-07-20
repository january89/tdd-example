package io.pretense.interfaces.api;

import io.pretense.domain.Article;
import io.pretense.infrastructure.jpa.ArticleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.pretense.DemoApplicationTests;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(DemoApplicationTests.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
public class ArticleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void 게시글_등록() throws Exception {

        //given
        String json = "{\"title\":\"제목\",\"body\":\"본문\"}";

        //when
        mvc.perform(post("/api/article").contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isCreated());
    }

    @Test(expected = NestedServletException.class)
    public void 게시글_제목이_없이_등록시_에러() throws Exception {
        //given
        String json = "{\"title\":\"테스트\",\"body\":null}";

        //when
        mvc.perform(post("/api/article").contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void 게시글_수정() throws Exception {

        //given
        Article article = createArticle("제목", "본문");
        String json = "{\"id\":"+article.getId()+",\"title\":\"수정제목\",\"body\":\"수정본문\"}";

        //when
        String body = mvc.perform(put("/api/article").contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThat(body, is("{\"id\":"+article.getId()+",\"title\":\"수정제목\",\"body\":\"수정본문\"}"));
    }

    @Test
    public void 게시글_전체_조회() throws Exception {

        //when * then
        mvc.perform(get("/api/article").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void 게시글_한건_조회() throws Exception {

        //given
        Article article = createArticle("조회제목", "조회본문");
        Long id = article.getId();

        //when
        String body = mvc.perform(get("/api/article/"+id).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //then
        assertThat(body, is("{\"id\":"+article.getId()+",\"title\":\"조회제목\",\"body\":\"조회본문\"}"));
    }

    private Article createArticle(String title, String body) {
        return articleRepository.save(new Article(title, body));
    }

}