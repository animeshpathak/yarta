{
    "Person": {
        "isSubjectOf": {
            "dataProperties": [
                "email",
                "firstName",
                "homepage",
                "lastName",
                "agentName",
                "userId"
            ],
            "objectProperties": {
                "knows": [
                    "Agent",
                    "Person",
                    "Group"
                ],
                "hasInterest": [
                    "Resource",
                    "Agent",
                    "Person",
                    "Group",
                    "Place",
                    "Event",
                    "Content",
                    "Topic"
                ],
                "isMemberOf": [
                    "Group"
                ],
                "creator": [
                    "Content"
                ],
                "isTagged": [
                    "Topic"
                ],
                "isLocated": [
                    "Place"
                ],
                "participatesTo": [
                    "Event"
                ]
            }
        },
        "isObjectOf": {
            "knows": [
                "Agent",
                "Person",
                "Group"
            ],
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Group": {
        "isSubjectOf": {
            "dataProperties": [
                "email",
                "homepage",
                "agentName"
            ],
            "objectProperties": {
                "knows": [
                    "Agent",
                    "Person",
                    "Group"
                ],
                "hasInterest": [
                    "Resource",
                    "Agent",
                    "Person",
                    "Group",
                    "Place",
                    "Event",
                    "Context",
                    "Topic"
                ],
                "isMemberOf": [
                    "Group"
                ],
                "creator": [
                    "Content"
                ],
                "isTagged": [
                    "Topic"
                ],
                "isLocated": [
                    "Place"
                ],
                "participatesTo": [
                    "Event"
                ]
            }
        },
        "isObjectOf": {
            "knows": [
                "Agent",
                "Person",
                "Group"
            ],
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ],
            "isMemberOf": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Agent": {
        "isSubjectOf": {
            "dataProperties": [
                "email",
                "homepage",
                "agentName"
            ],
            "objectProperties": {
                "knows": [
                    "Agent",
                    "Person",
                    "Group"
                ],
                "hasInterest": [
                    "Resource",
                    "Agent",
                    "Person",
                    "Group",
                    "Place",
                    "Event",
                    "Context",
                    "Topic"
                ],
                "isMemberOf": [
                    "Group"
                ],
                "creator": [
                    "Content"
                ],
                "isTagged": [
                    "Topic"
                ],
                "isLocated": [
                    "Place"
                ],
                "participatesTo": [
                    "Event"
                ]
            }
        },
        "isObjectOf": {
            "knows": [
                "Agent",
                "Person",
                "Group"
            ],
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Place": {
        "isSubjectOf": {
            "dataProperties": [
                "placeName",
                "latitude",
                "longitude"
            ],
            "objectProperties": {
                "isLocated": [
                    "Place"
                ],
                "isTagged": [
                    "Topic"
                ]
            }
        },
        "isObjectOf": {
            "isLocated": [
                "Event",
                "Place",
                "Agent",
                "Group",
                "Person"
            ],
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Resource": {
        "isSubjectOf": {
            "dataProperties": [],
            "objectProperties": {}
        },
        "isObjectOf": {
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Event": {
        "isSubjectOf": {
            "dataProperties": [
                "eventTitle",
                "description"
            ],
            "objectProperties": {
                "isLocated": [
                    "Place"
                ],
                "isTagged": [
                    "Topic"
                ]
            }
        },
        "isObjectOf": {
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ],
            "participatesTo": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Content": {
        "isSubjectOf": {
            "dataProperties": [
                "contentTitle",
                "source",
                "identifier",
                "format"
            ],
            "objectProperties": {
                "isTagged": [
                    "Topic"
                ]
            }
        },
        "isObjectOf": {
            "creator": [
                "Agent",
                "Person",
                "Group"
            ],
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ]
        }
    },
    "Topic": {
        "isSubjectOf": {
            "dataProperties": [
                "topicTitle"
            ],
            "objectProperties": {
                "isTagged": [
                    "Topic"
                ]
            }
        },
        "isObjectOf": {
            "hasInterest": [
                "Agent",
                "Person",
                "Group"
            ],
            "isTagged": [
                "Resource",
                "Agent",
                "Person",
                "Group",
                "Event",
                "Place",
                "Content",
                "Topic"
            ]
        }
    }
}