package xyz.iknowca.iknowdevspring.domain.notice.comment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.Role
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.exception.AccountException
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService
import xyz.iknowca.iknowdevspring.domain.notice.controller.CommentController
import xyz.iknowca.iknowdevspring.domain.notice.entity.CommentDto
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.CommentServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl
import java.util.*

class CommentPostTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode {
        return IsolationMode.InstancePerTest
    }

    init {

        val accountService = mockk<AccountService>()
        val noticeRepository = mockk<NoticeRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(
            CommentController(commentService = CommentServiceImpl(NoticeServiceImpl(noticeRepository, accountService), accountService))
        ).build()

        val commentContent: String = "this is test comment"
        val writer: Account = Account("test@eamil.com", "testpassword", roles = listOf(Role(RoleType.ADMIN), Role(RoleType.NORMAL)), 1L)
        val notice: Notice = Notice(
            "test title", "test notice", 1L
        )
        val comment: CommentDto = CommentDto(null, null, commentContent)
        val requestBody = jacksonObjectMapper().writeValueAsString(comment)

        val nonExistNoticeId = 2L
        val nonExistAccountId = 2L

        Given("댓글이 작성되는 경우") {

            every { accountService.findAccount(writer.id.toString()) } returns writer
            every { accountService.checkRole(writer, any())} returns true
            every { noticeRepository.findById(notice.id) } returns Optional.of(notice)
            every { noticeRepository.save(notice) } returns notice

            every { noticeRepository.findById(nonExistNoticeId) } returns Optional.empty()
            every { accountService.findAccount(nonExistAccountId.toString()) } throws AccountException("")

            When("정상적인 사용자라면") {
                When("정상적인 게시글이라면") {
                    val result = mockMvc.post("/notice/${notice.id}/comment") {
                        content = requestBody
                        contentType = MediaType.APPLICATION_JSON
                        accept = MediaType.APPLICATION_JSON
                        header(HttpHeaders.AUTHORIZATION, writer.id.toString())
                    }
                    result.andDo { print() }
                    Then("status code: 200ok") {
                        result.andExpect { status { isOk() } }
                    }
                    Then("status: success") {
                        result.andExpect { jsonPath("\$.status") { value("success") } }
                    }
                }
                When("존재하는 게시글이 아니라면") {
                    val result = mockMvc.post("/notice/${nonExistNoticeId}/comment") {
                        content = requestBody
                        contentType = MediaType.APPLICATION_JSON
                        accept = MediaType.APPLICATION_JSON
                        header(HttpHeaders.AUTHORIZATION, writer.id.toString())
                    }
                    Then("status code: no content") {
                        result.andExpect { status { isNoContent() } }
                    }
                }
            }
            When("정상적인 사용자가 아니라면") {
                mockMvc.post("/notice/${notice.id}/comment") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, "2")
                }
                Then("status code: unauthorized") {

                }
            }
        }
    }
}