package com.aengdulab.ticket;

import com.aengdulab.ticket.service.MemberTicketService;
import static org.assertj.core.api.Assertions.assertThat;

import com.aengdulab.ticket.domain.Member;
import com.aengdulab.ticket.domain.Ticket;
import com.aengdulab.ticket.repository.MemberRepository;
import com.aengdulab.ticket.repository.MemberTicketRepository;
import com.aengdulab.ticket.repository.TicketRepository;
import com.aengdulab.ticket.support.TimeMeasure;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@SuppressWarnings("NonAsciiCharacters")
class MissionTest {

    private static final Logger log = LoggerFactory.getLogger(MissionTest.class);
    private static final int MEMBER_TICKET_COUNT_MAX = 2;

    @Autowired
    private MemberTicketService memberTicketService;

    @Autowired
    private MemberTicketRepository memberTicketRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        memberTicketRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
    }

    /*
     * TODO: 테스트를 성공시키자!
     */
    @Test
    void 티켓이_모두_팔렸지만_재고가_0이_아닌_문제() {
        int ticketQuantity = 10;
        int memberCount = 5;
        Ticket ticket = createTicket("목성행", ticketQuantity);
        List<Member> members = createMembers(memberCount);

        int threadCount = memberCount * MEMBER_TICKET_COUNT_MAX;
        TimeMeasure.measureTime(() -> {
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
                sendMultipleRequests(executorService, members, ticket);
            }
        });

        assertThat(getTicketQuantity(ticket)).isZero();
        for (Member member : members) {
            assertThat(getMemberTicketCount(member)).isEqualTo(MEMBER_TICKET_COUNT_MAX);
        }
    }

    /*
     * TODO: 테스트를 성공시키자!
     */
    @Test
    void 멤버가_구매_가능_한도를_초과하여_티켓을_발급받은_문제() {
        int ticketQuantity = 30;
        int memberCount = 5;
        int ticketIssueCount = 3 * MEMBER_TICKET_COUNT_MAX;
        Ticket jupiterTicket = createTicket("목성행", ticketQuantity);
        Ticket marsTicket = createTicket("화성행", ticketQuantity);
        List<Member> members = createMembers(memberCount);

        int threadCount = memberCount * ticketIssueCount;
        TimeMeasure.measureTime(() -> {
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
                sendMultipleRequests(executorService, members, jupiterTicket, marsTicket);
            }
        });

        assertThat(getTicketQuantity(jupiterTicket)).isNotNegative();
        assertThat(getTicketQuantity(marsTicket)).isNotNegative();
        for (Member member : members) {
            assertThat(getMemberTicketCount(member)).isEqualTo(MEMBER_TICKET_COUNT_MAX);
        }
    }

    /*
     * TODO: 테스트를 성공시키자!
     */
    @Test
    void 티켓_재고_보다_많은_구매_요청이_들어온_문제() {
        int ticketQuantity = 10;
        int memberCount = 10;
        Ticket ticket = createTicket("목성행", ticketQuantity);
        List<Member> members = createMembers(memberCount);

        int threadCount = memberCount * MEMBER_TICKET_COUNT_MAX;
        TimeMeasure.measureTime(() -> {
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
                sendMultipleRequests(executorService, members, ticket);
            }
        });

        assertThat(getTicketQuantity(ticket)).isZero();
        for (Member member : members) {
            assertThat(getMemberTicketCount(member)).isLessThanOrEqualTo(MEMBER_TICKET_COUNT_MAX);
        }
    }

    private void sendMultipleRequests(ExecutorService executorService, List<Member> members, Ticket... tickets) {
        AtomicInteger succeedRequestCount = new AtomicInteger(0);
        AtomicInteger failRequestCount = new AtomicInteger(0);

        for (Member member : members) {
            for (int i = 0; i < MEMBER_TICKET_COUNT_MAX; i++) {
                executorService.submit(() -> {
                    try {
                        memberTicketService.issue(member.getId(), getRandomTicket(tickets).getId());
                        succeedRequestCount.incrementAndGet();
                    } catch (Exception e) {
                        log.error("멤버 티켓 발행 중 오류 발생", e);
                        failRequestCount.incrementAndGet();
                    }
                });
            }
        }

        log.info("성공한 요청 수 : {}", succeedRequestCount.get());
        log.info("실패한 요청 수 : {}", failRequestCount.get());
    }

    private Ticket getRandomTicket(Ticket... tickets) {
        int ticketOrder = (int) (Math.random() * tickets.length);
        return tickets[ticketOrder];
    }

    private long getTicketQuantity(Ticket ticket) {
        return ticketRepository.findById(ticket.getId()).orElseThrow().getQuantity();
    }

    private int getMemberTicketCount(Member member) {
        return memberTicketRepository.countByMember(member);
    }

    private Ticket createTicket(String ticketName, long quantity) {
        return ticketRepository.save(new Ticket(ticketName, quantity));
    }

    private List<Member> createMembers(int memberCount) {
        return IntStream.range(0, memberCount)
            .mapToObj(sequence -> memberRepository.save(new Member("멤버" + sequence)))
            .toList();
    }
}
