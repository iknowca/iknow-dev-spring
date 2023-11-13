package xyz.iknowca.iknowdevspring.domain.notice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.haveValues
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.Role
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.controller.NoticeCotroller
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.entity.NoticeDto
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl
import java.util.*
import kotlin.math.min
import kotlin.reflect.typeOf

@SpringBootTest
class NoticeTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest


    init {

        val accountRepository = mockk<AccountRepository>()
        val noticeRepository = mockk<NoticeRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(
            NoticeCotroller(
                NoticeServiceImpl(
                    noticeRepository,
                    AccountServiceImpl(accountRepository)
                )
            )
        ).build()

        val title = "제목"
        val requestContent = "본문"
        val noticeId = 1L
        val requestDto = NoticeDto(title, requestContent)
        val requestBody = jacksonObjectMapper().writeValueAsString(requestDto)

        val adminAccount =
            Account("adminAccount", "testPassword", listOf(Role(RoleType.NORMAL), Role(RoleType.ADMIN)), 1L)
        val normalAccount = Account("normalAccount", "testPassword", listOf(Role(RoleType.NORMAL)), 2L)


        Given("공지사항 게시를 요청받았을때") {
            every { noticeRepository.save(any()) } returns Notice(title, requestContent, noticeId)

            every { accountRepository.findById(adminAccount.id) } returns Optional.of(adminAccount)
            every { accountRepository.findById(normalAccount.id) } returns Optional.of(normalAccount)

            every { accountRepository.findRoleByAccount(adminAccount) } returns adminAccount.roles.map { r: Role -> r.roleType }
            every { accountRepository.findRoleByAccount(normalAccount) } returns normalAccount.roles.map { r: Role -> r.roleType }

            When("ADMIN 유저의 요청이라면") {
                val result = mockMvc.post("/notice") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                    accept = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, adminAccount.id.toString())
                }
                Then("상태코드는 OK") {
                    result.andExpect {
                        status { isOk() }
                    }
                }
                Then("status:success, noticeId:${noticeId}") {
                    result.andExpect {
                        jsonPath("$.status") { value("success") }
                    }.andExpect {
                        jsonPath("$.noticeId") { value(noticeId.toString()) }
                    }
                }
                result.andDo { print() }
            }
            When("NORMAL 유저의 요청이라면") {
                val result = mockMvc.post("/notice") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                    accept = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, normalAccount.id.toString())
                }
                Then("상태코드는 403 Forbiden") {
                    result.andExpect {
                        status { isForbidden() }
                    }
                }
            }
            When("로그인하지 않은 사용자의 요청이라면") {
                val result = mockMvc.post("/notice") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                    accept = MediaType.APPLICATION_JSON
                }
                Then("상태코드는 401 Unauthorized") {
                    result.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }
        }

        val notice = Notice("title", "content", 1L)

        Given("사용자가 공지사항을 조회할 경우") {
            every { noticeRepository.findById(any()) } returns Optional.empty()
            every { noticeRepository.findById(noticeId) } returns Optional.of(notice)
            When("존재하는 공지사항의 정보라면") {
                val result = mockMvc.get("/notice/${notice.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }
                Then("상태코드는 OK") {
                    result.andExpect { status { isOk() } }
                }
                Then("notice엔티티를 반환한다") {
                    result.andExpectAll {
                        jsonPath("$.id") { value(notice.id) }
                        jsonPath("$.title") { value(notice.title) }
                        jsonPath("$.content") { value(notice.content) }
                    }
                }
            }
            When("존재하지 않는 공지사항의 정보라면") {
                val result = mockMvc.get("/notice/2") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }
                Then("상태코드는 204 NoContent") {
                    result.andExpect { status { HttpStatus.NO_CONTENT } }
                }
            }
        }

        Given("사용자가 공지사항 size개 page페이지를 요청하는 경우.") {

            val size: Int = 5

            val page0: Int = 0
            val pageableNormal: Pageable = PageRequest.of(page0, size)
            val noticeListNormal = mutableListOf<Notice>()
            for (i in 1L..5L) {
                noticeListNormal.add(Notice("title${i}", "content${i}", i))
            }
            every { noticeRepository.findAll(pageableNormal) } returns PageImpl<Notice>(noticeListNormal)

            val page1: Int = 1
            val pageableLeak: Pageable = PageRequest.of(page1, size)
            val noticeListLeak = mutableListOf<Notice>()
            for (i in 6L..9L) {
                noticeListLeak.add(Notice("title${i}", "content${i}", i))
            }
            every { noticeRepository.findAll(pageableLeak) } returns PageImpl<Notice>(noticeListLeak)

            val page2: Int = 2
            val pageableVoid: Pageable = PageRequest.of(page2, size)
            val noticeListVoid = mutableListOf<Notice>()
            every { noticeRepository.findAll(pageableVoid) } returns PageImpl<Notice>(noticeListVoid)

            When("page의 공지사항이 size개 있을 경우") {
                val result = mockMvc.get("/notice/list?page=${page0}&size=${size}") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andDo { print() }
                Then("size개의 공지사항리스트를 반환한다.") {
                    result.andExpect {
                        status { isOk() }
                    }
                    for (i in 0 until size) {
                        result.andExpect {
                            jsonPath("$..content[${i}].title") { value(noticeListNormal[i].title) }
                            jsonPath("$..content[${i}].content") { value(noticeListNormal[i].content) }
                        }
                    }
                    result.andExpect {
                        jsonPath("$.size") { value(size) }
                        jsonPath("$..content.length()") { value(5) }
                    }
                }
            }
            When("page의 공지사항이 size 미만인 경우") {
                val result = mockMvc.get("/notice/list?page=${page1}&size=${size}") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andDo { print() }
                Then("존재하는 만큼의 공지사항 리스트를 반환한다.") {
                    result.andExpect { status { isOk() } }
                    for (i in 0 until 4) {
                        result.andExpect {
                            jsonPath("$..content[${i}].title") { value(noticeListLeak[i].title) }
                            jsonPath("$..content[${i}].content") { value(noticeListLeak[i].content) }
                        }
                    }
                    result.andExpect {
                        jsonPath("$.size") { value(4) }
                        jsonPath("$..content.length()") { value(4) }
                    }
                }
            }
            When("page의 공지사항이 0개인 경우") {
                val result = mockMvc.get("/notice/list?page=${page2}&size=${size}") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }
                Then("상태코드 204 NoContent") {
                    result.andExpect { status { HttpStatus.NO_CONTENT } }
                }
            }
        }
    }
}