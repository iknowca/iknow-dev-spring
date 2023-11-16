package xyz.iknowca.iknowdevspring.domain.notice

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import xyz.iknowca.iknowdevspring.domain.notice.controller.NoticeController
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice
import xyz.iknowca.iknowdevspring.domain.notice.repository.NoticeRepository
import xyz.iknowca.iknowdevspring.domain.notice.service.NoticeServiceImpl

class NoticeReadListTest: BehaviorSpec() {
    override fun isolationMode(): IsolationMode? {
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