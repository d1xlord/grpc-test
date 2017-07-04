#include <algorithm>
#include <cmath>
#include <iostream>
#include <string>

#include <grpc/grpc.h>
#include <grpc++/server.h>
#include <grpc++/server_builder.h>
#include <grpc++/server_context.h>
#include <grpc++/security/server_credentials.h>
#include "extractor_service.grpc.pb.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::ServerReader;
using grpc::ServerReaderWriter;
using grpc::ServerWriter;
using grpc::Status;

using extractor_service::InputFinger;
using extractor_service::ExtractedFinger;
using extractor_service::ExtractorService;

class ExtractorServiceImpl final : public ExtractorService::Service {
	Status Extract (ServerContext* context, const InputFinger* inputFinger, ExtractedFinger* extractedFinger) override {
		std::cout << "Extract called with inputFinger, values: " << inputFinger->format() << " // " << inputFinger->data() << std::endl;
		float quality = 9.7;
		std::string indexStr = "IndexIndexIndex";
		std::string exDataStr = inputFinger->data() + "ExtractedDataExtractedDataExtractedData";
		const byte *index = indexStr.c_str();
		const byte *exData = exDataStr.c_str();
		
		extractedFinger->set_quality(quality);
		extractedFinger->set_index(index);
		extractedFinger->set_data(exData);
		return Status::OK;
	}
};

void RunServer() {
	std::string server_addr("0.0.0.0:50551");
	ExtractorServiceImpl service;
	
	ServerBuilder builder;
	builder.AddListeningPort(server_addr, grpc::InsecureServerCredentials());
	builder.RegisterService(&service);
	std::unique_ptr<Server> server(builder.BuildAndStart());
	std::cout << "Server listening on " << server_addr << std::endl;
	server->Wait();
}

int main(int argc, char** argv)
{
	RunServer();
	
	return 0;
}

