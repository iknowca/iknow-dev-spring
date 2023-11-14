package xyz.iknowca.iknowdevspring.domain.notice

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.controller.NoticeCotroller
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl
import java.util.*

class NoticeReadTest:BehaviorSpec() {
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

        val notice = Notice("title", "content", 1L)

        Given("사용자가 공지사항을 조회할 경우") {
            every { noticeRepository.findById(any()) } returns Optional.empty()
            every { noticeRepository.findById(notice.id) } returns Optional.of(notice)
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
    }
}