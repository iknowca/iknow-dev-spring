package xyz.iknowca.iknowdevspring.domain.notice

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.Role
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.controller.NoticeController
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl
import java.util.*

class NoticeDeleteTest:BehaviorSpec() {

    override fun isolationMode(): IsolationMode {
        return IsolationMode.InstancePerTest
    }

    init {

        val accountRepository = mockk<AccountRepository>()
        val noticeRepository = mockk<NoticeRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(
            NoticeController(
                NoticeServiceImpl(
                    noticeRepository,
                    AccountServiceImpl(accountRepository)
                )
            )
        ).build()




        Given("사용자가 공지사항 삭제를 요청하면") {

            val notice = Notice("testTitle", "testContent", 1L)
            val adminAccount =
                Account("admin_email", "admin_password", listOf(Role(RoleType.ADMIN), Role(RoleType.NORMAL)), 1L)
            val nonExistNoticeId = 2L
            val normalAccount = Account("normal_email", "normal_password", listOf( Role(RoleType.NORMAL)),2L)

            every { accountRepository.findById(adminAccount.id) } returns Optional.of(adminAccount)
            every { accountRepository.findById(normalAccount.id) } returns Optional.of(normalAccount)
            every { noticeRepository.findById(notice.id) } returns Optional.of(notice)
            every { accountRepository.findRoleByAccount(adminAccount) } returns adminAccount.roles.map { role->role.roleType }
            every { accountRepository.findRoleByAccount(normalAccount) } returns normalAccount.roles.map { role->role.roleType }
            every { noticeRepository.findById(nonExistNoticeId) } returns Optional.empty()
            every { noticeRepository.delete(any()) } just  Runs

            When("ADMIN 사용자이라면") {

                When("존재하는 공지사항이라면") {
                    val result = mockMvc.delete("/notice/${notice.id}") {
                        header(HttpHeaders.AUTHORIZATION, adminAccount.id.toString())
                    }
                    Then("status code 200 ok") {
                        result.andExpect { status { isOk() } }
                    }
                    Then("status:success") {
                        result.andExpect { jsonPath("$.status") { value("success") } }
                    }
                    result.andDo { print() }

                }
                When("존재하지 않는 공지사항이라면") {
                    val result = mockMvc.delete("/notice/${nonExistNoticeId}") {
                        header(HttpHeaders.AUTHORIZATION, adminAccount.id.toString())
                    }
                    Then("status code no content") {
                        result.andExpect { status { isNoContent() } }
                    }
                }
            }
            When("ADMIN 사용자가 아니라면") {
                val result = mockMvc.delete("/notice/${notice.id}") {
                    header(HttpHeaders.AUTHORIZATION, normalAccount.id.toString())
                }
                Then("status code FORBIDDEN") {
                    result.andExpect { status { isForbidden() } }
                }
            }
        }
    }
}