package xyz.iknowca.iknowdevspring.domain.notice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.Role
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.controller.NoticeCotroller
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl
import java.util.*

class NoticeUpdateTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode? {
        return IsolationMode.InstancePerTest
    }

    init {
        val noticeRepository = mockk<NoticeRepository>()
        val accountRepository = mockk<AccountRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(
            NoticeCotroller(
                NoticeServiceImpl(
                    noticeRepository,
                    AccountServiceImpl(accountRepository)
                )
            )
        ).build()

        val adminAccount = Account("admin@", "admin", listOf(Role(RoleType.ADMIN), Role(RoleType.NORMAL)), 1L)
        val notice = Notice("title", "content", 1L)
        val modifiedNotice = Notice("modified_title", "modified_content", 1L)

        val normalAccount = Account("admin@", "admin", listOf(Role(RoleType.NORMAL)), 2L)
        val nonExistNoticeId = 2L

        val requestBody = jacksonObjectMapper().writeValueAsString(modifiedNotice)

        Given("사용자가 공지사항을 수정하는 경우") {

            every { accountRepository.findById(adminAccount.id) } returns Optional.of(adminAccount)
            every { accountRepository.findById(normalAccount.id) } returns Optional.of(normalAccount)
            every { accountRepository.findRoleByAccount(adminAccount) } returns adminAccount.roles.map { role -> role.roleType }
            every { accountRepository.findRoleByAccount(normalAccount) } returns normalAccount.roles.map { role -> role.roleType }
            every { noticeRepository.findById(notice.id) } returns Optional.of(notice)
            every { noticeRepository.findById(nonExistNoticeId) } returns Optional.empty()
            every { noticeRepository.save(any()) } returns modifiedNotice

            When("ADMIN계정이 존재하는 공지사항을 수정하는 경우") {
                val result = mockMvc.put("/notice/${notice.id}") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, adminAccount.id.toString())
                }
                Then("status code: 200ok") {
                    result.andExpect { status { isOk() } }
                }
                Then("status:success") {
                    result.andExpect { jsonPath("$.status") { value("success") } }
                }
                Then("noticeId:") {
                    result.andExpect { jsonPath("$.noticeId") {value("${notice.id}")} }
                }
            }
            When("ADMIN계정이 존재하지 않는 공지사항을 수정하는 경우") {
                val result = mockMvc.put("/notice/${nonExistNoticeId}") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, adminAccount.id.toString())
                }
                Then("status code: no content") {
                    result.andExpect { status { isNoContent() } }
                }
            }
            When("NORMAL계정이 존재하는 공지사항을 수정하는 경우") {
                val result = mockMvc.put("/notice/${notice.id}") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, normalAccount.id.toString())
                }
                Then("status code: forbidden") {
                    result.andExpect { status { isForbidden() } }
                }
            }
        }
    }
}